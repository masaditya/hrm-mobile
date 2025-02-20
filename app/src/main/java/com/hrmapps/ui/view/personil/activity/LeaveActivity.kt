package com.hrmapps.ui.view.personil.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hrmapps.R
import com.hrmapps.data.api.RetrofitBuilder
import com.hrmapps.data.repository.leave.ListLeaveRepository
import com.hrmapps.databinding.ActivityLeaveBinding
import com.hrmapps.ui.adapter.LeaveAdapter
import com.hrmapps.ui.viewmodel.leave.LeaveViewModel
import com.hrmapps.ui.viewmodel.leave.LeaveViewModelFactory

class LeaveActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaveBinding
    private lateinit var leaveAdapter: LeaveAdapter
    private lateinit var viewModel: LeaveViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var token: String
    private var userId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLeaveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.mToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.fabAddForm.setOnClickListener {
            startActivity(Intent(this, AddNewFormLeaveActivity::class.java))
        }
        setupView()
        setupViewModel()
        setupRecyclerView()
    }

    private fun setupView() {
        sharedPreferences = getSharedPreferences("userPref", MODE_PRIVATE)
        token = sharedPreferences.getString("token", "") ?: ""
        userId = sharedPreferences.getInt("userId", 0)
    }

    private fun setupRecyclerView() {
        leaveAdapter = LeaveAdapter()
        binding.rvLeave.apply {
            adapter = leaveAdapter
            layoutManager = LinearLayoutManager(this@LeaveActivity)
        }


    }

    private fun setupViewModel() {
        val apiService = RetrofitBuilder.apiService
        val repository = ListLeaveRepository(apiService)

        val factory = LeaveViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[LeaveViewModel::class.java]

        viewModel.loading.observe(this) { isLoading ->
            binding.loadingBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(this, errorMessage.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.leaveData.observe(this) { leaveList ->
            if (leaveList.isNullOrEmpty()) {
                binding.layoutEmpty.visibility = View.VISIBLE
            } else {
                leaveAdapter.setData(leaveList)
                binding.layoutEmpty.visibility = View.GONE
            }
        }
        viewModel.fetchLeaveData(token, userId)

    }
    override fun onResume() {
        super.onResume()
        viewModel.fetchLeaveData(token, userId)
    }

}
