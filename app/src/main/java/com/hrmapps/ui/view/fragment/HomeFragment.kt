package com.hrmapps.ui.view.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hrmapps.R
import com.hrmapps.data.api.RetrofitBuilder
import com.hrmapps.data.repository.AuthRepository
import com.hrmapps.data.repository.CheckInStatusRepository
import com.hrmapps.databinding.FragmentHomeBinding
import com.hrmapps.ui.view.activity.LeaveActivity
import com.hrmapps.ui.view.activity.PresentCheckInActivity
import com.hrmapps.ui.view.activity.PresentCheckOutActivity
import com.hrmapps.ui.view.activity.RequestActivity
import com.hrmapps.ui.viewmodel.AuthViewModelFactory
import com.hrmapps.ui.viewmodel.CheckInStatusViewModel
import com.hrmapps.ui.viewmodel.CheckInStatusViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CheckInStatusViewModel
    private var setCheckIn: Boolean = false
    private lateinit var sharedPreferences: SharedPreferences

    private val handler = Handler()
    private val runnable: Runnable = object : Runnable {
        override fun run() {
            updateTimeAndDate()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("isLoggedIn", AppCompatActivity.MODE_PRIVATE)
        setupViewModel()
        observeCheckInStatus()
        setupUI()
        handler.post(runnable)
    }

    private fun setupViewModel() {
        val apiService = RetrofitBuilder.apiService
        val repository = CheckInStatusRepository(apiService)
        val viewModelFactory = CheckInStatusViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[CheckInStatusViewModel::class.java]

        val token = "Bearer " + sharedPreferences.getString("token", "")
        val userId = sharedPreferences.getInt("userId", 0)
        viewModel.getCheckInStatus(token, userId)
    }


    private fun setupUI() {

        binding.linearLayout2.apply {
        }
        binding.btCheckpoint.setOnClickListener {

        }
        binding.btPresent.setOnClickListener {
            if (setCheckIn){
                startActivity(Intent(requireContext(), PresentCheckOutActivity::class.java))
            }else{
                startActivity(Intent(requireContext(), PresentCheckInActivity::class.java))
            }

        }
        binding.btLeave.setOnClickListener {
            startActivity(Intent(requireContext(), LeaveActivity::class.java))
        }
        binding.btRequest.setOnClickListener {
            startActivity(Intent(requireContext(), RequestActivity::class.java))
        }
    }
    private fun observeCheckInStatus() {
        viewModel.checkInStatus.observe(viewLifecycleOwner) { response ->
            if (response?.data != null) {
                val clockInTime = response.data.clock_in_time
                setCheckIn = !clockInTime.isNullOrEmpty()
                Log.d("HomeFragment", "Clock In Time: $clockInTime, Set Check In: $setCheckIn")
                binding.tvCheckIn.setTextColor(requireContext().getColor(R.color.green))
            } else {
                setCheckIn = false
                Log.d("HomeFragment", "Response or data is null. Set Check In: $setCheckIn")
            }
        }
    }



    private fun updateTimeAndDate() {
        val calendar = Calendar.getInstance()
        val hours = calendar.get(Calendar.HOUR)
        val minutes = calendar.get(Calendar.MINUTE)
        val seconds = calendar.get(Calendar.SECOND)
        val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"

        val dateFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
        val date = dateFormat.format(calendar.time)

        binding.tvFormatTime.text = amPm
        binding.tvHours.text = String.format("%02d", hours)
        binding.tvMinutes.text = String.format("%02d", minutes)
        binding.tvSecond.text = String.format("%02d", seconds)
        binding.tvDate.text = date
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(runnable)
        _binding = null
    }
}