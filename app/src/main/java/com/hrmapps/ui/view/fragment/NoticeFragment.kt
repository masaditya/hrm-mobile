package com.hrmapps.ui.view.fragment

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hrmapps.databinding.FragmentNoticeBinding
import com.hrmapps.ui.adapter.NoticeAdapter
import com.hrmapps.ui.viewmodel.notice.NoticeViewModel
import com.hrmapps.ui.viewmodel.notice.NoticeViewModelFactory
import com.hrmapps.data.api.RetrofitBuilder
import com.hrmapps.data.model.notice.Notice
import com.hrmapps.data.repository.notice.NoticeRepository
import com.hrmapps.ui.adapter.ShimmerNoticeAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NoticeFragment : Fragment() {

    private lateinit var binding: FragmentNoticeBinding
    private lateinit var noticeViewModel: NoticeViewModel
    private lateinit var adapter: NoticeAdapter
    private lateinit var shimmerAdapter: ShimmerNoticeAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private var token: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoticeBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences("userPref", MODE_PRIVATE)
        setupRecyclerView()
        setupViewModel()
        val userId = sharedPreferences.getInt("userId", 0)
        token = sharedPreferences.getString("token", "")
        lifecycleScope.launch {
            noticeViewModel.fetchNotices(token!!, userId)
        }

        observeLiveData()
        return binding.root
    }

    private fun setupRecyclerView() {
        binding.rvNotice.layoutManager = LinearLayoutManager(requireContext())
        adapter = NoticeAdapter(emptyList()) { notice ->
            onNoticeClicked(notice)
        }
        binding.rvNotice.adapter = adapter
    }

    private fun setupViewModel() {
        val apiService = RetrofitBuilder.apiService
        val repository = NoticeRepository(apiService)
        val factory = NoticeViewModelFactory(repository)
        noticeViewModel = ViewModelProvider(this, factory).get(NoticeViewModel::class.java)
    }

    private fun observeLiveData() {
        noticeViewModel.notices.observe(viewLifecycleOwner) { notices ->
            if (notices.isNotEmpty()) {
                adapter.updateData(notices)
                binding.layoutEmpty.visibility = View.GONE
            }
        }

        noticeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                startShimmer()
            } else {
                lifecycleScope.launch {
                    delay(1000)
                    stopShimmer()
                }

            }
        }

        noticeViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                showToast(error.toString())
            }
        }
    }
    private fun startShimmer() {
        val dummyData = List(10) { index ->
            Notice(
                notice_id = index,
                user_id = 0,
                read = 4,
                heading = "",
                date = "",
                time = ""
            )
        }


        binding.shimmerRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        shimmerAdapter = ShimmerNoticeAdapter(dummyData)
        binding.shimmerRecyclerView.adapter = shimmerAdapter

        binding.shimmerRecyclerView.visibility = View.VISIBLE
        binding.shimmerLayout.startShimmer()
        binding.rvNotice.visibility = View.GONE
        binding.shimmerLayout.visibility = View.VISIBLE
        binding.layoutEmpty.visibility = View.GONE
    }


    private fun stopShimmer() {
        binding.shimmerLayout.stopShimmer()
        binding.rvNotice.visibility = View.VISIBLE
        binding.shimmerLayout.visibility = View.GONE
        binding.layoutEmpty.visibility = View.GONE
        if (adapter.itemCount == 0) {
            binding.layoutEmpty.visibility = View.VISIBLE
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun onNoticeClicked(notice: Notice) {
        if (notice.read != 1){
            noticeViewModel.markNoticeAsRead(token!!, notice.notice_id)
        }
        val intent = Intent(requireContext(), DetailNoticeActivity::class.java)
        intent.putExtra("noticeId", notice.notice_id)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        noticeViewModel.fetchNotices(token!!, sharedPreferences.getInt("userId", 0))
    }
}
