package com.hrmapps.ui.view.fragment

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hrmapps.R
import com.hrmapps.data.api.RetrofitBuilder
import com.hrmapps.data.repository.attendance.AttendanceRepository
import com.hrmapps.data.repository.attendance.CheckInStatusRepository
import com.hrmapps.databinding.FragmentHistoryBinding
import com.hrmapps.ui.adapter.HistoryAdapter
import com.hrmapps.ui.viewmodel.attendance.AttendanceViewModel
import com.hrmapps.ui.viewmodel.attendance.AttendanceViewModelFactory
import com.hrmapps.ui.viewmodel.attendance.CheckInStatusViewModel
import com.hrmapps.ui.viewmodel.attendance.CheckInStatusViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalTime
import java.util.Locale

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AttendanceViewModel
    private lateinit var checkStatusViewModel: CheckInStatusViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var historyAdapter: HistoryAdapter

    private val months = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("userPref", MODE_PRIVATE)
        setupViewModel()
        setupSpinner()
        setupRecyclerView()
        observeCheckStatus()
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter()
        binding.recyclerViewAttendance.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewAttendance.adapter = historyAdapter
    }

    private fun setupViewModel() {
        val apiService = RetrofitBuilder.apiService
        val repositoryAttendance = AttendanceRepository(apiService)
        val repositoryCheckStatus = CheckInStatusRepository(apiService)

        val checkStatusViewModelFactory = CheckInStatusViewModelFactory(repositoryCheckStatus)
        checkStatusViewModel = ViewModelProvider(this, checkStatusViewModelFactory)[CheckInStatusViewModel::class.java]

        val token = sharedPreferences.getString("token", "")
        val userId = sharedPreferences.getInt("userId", 0)
        token?.let { checkStatusViewModel.getCheckInStatus(it, userId) }
        viewModel = ViewModelProvider(this, AttendanceViewModelFactory(repositoryAttendance))[AttendanceViewModel::class.java]
    }

    private fun observeAttendanceData(selectedMonth: String?) {
        val token = sharedPreferences.getString("token", "") ?: return
        val workingFrom = "office"
        val userId = sharedPreferences.getInt("userId", 0)
        viewModel.fetchAttendanceData(token, workingFrom, userId)

        viewModel.attendanceData.observe(viewLifecycleOwner) { attendanceList ->
            val filteredList = if (selectedMonth != null) {
                attendanceList.filter { attendance ->
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val date = dateFormat.parse(attendance.clock_in_time)

                    val monthName = if (date != null) {
                        SimpleDateFormat("MMMM", Locale.US).format(date)
                    } else {
                        ""
                    }

                    monthName.equals(selectedMonth, ignoreCase = true)
                }
            } else {
                attendanceList
            }
            if (filteredList.isEmpty()) {
                historyAdapter.submitList(emptyList())
                binding.recyclerViewAttendance.visibility = View.GONE
            } else {
                historyAdapter.submitList(filteredList)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
           if (!errorMessage.isNullOrEmpty()){
               Toast.makeText(requireContext(), errorMessage.toString(), Toast.LENGTH_SHORT).show()
           }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.recyclerViewAttendance.visibility = View.GONE
                binding.shimmerListHistory.startShimmer()
                binding.tvNoHistory.visibility = View.GONE
                binding.shimmerListHistory.visibility = View.VISIBLE
            } else {
                lifecycleScope.launch {
                    delay(1000)
                    binding.shimmerListHistory.stopShimmer()
                    binding.shimmerListHistory.visibility = View.GONE
                    if (historyAdapter.itemCount == 0) {
                        binding.tvNoHistory.visibility = View.VISIBLE
                        binding.recyclerViewAttendance.visibility = View.GONE
                    }else{
                        binding.tvNoHistory.visibility = View.GONE
                        binding.recyclerViewAttendance.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun observeCheckStatus() {
        checkStatusViewModel.checkInStatus.observe(viewLifecycleOwner) { response ->
            if (response?.data != null) {
                if (response.data.clock_in_time != null) {
                    val clockCheckIn = response.data.clock_in_time
                    val clockInTime = getHourFromDateString(clockCheckIn)
                    binding.tvCheckIn.text = "Check In"
                    binding.tvClockIn.text = changeHourAmPmFormat(clockInTime)
                    binding.tvCheckIn.setTextColor(requireContext().getColor(R.color.green))
                    if (response.data.clock_out_time != null) {
                        val clockCheckOut = response.data.clock_out_time
                        val clockOutTime = getHourFromDateString(clockCheckOut)
                        binding.tvCheckOut.text = "Check Out"
                        binding.tvClockOut.text = changeHourAmPmFormat(clockOutTime)
                        binding.tvCheckOut.setTextColor(requireContext().getColor(R.color.green))
                        binding.linearLayout4.visibility = View.VISIBLE
                        binding.tvTotalWorkTimeToday.text = calculateTotalHoursToday(clockInTime, clockOutTime)
                    } else {
                        binding.tvCheckOut.text = "-"
                    }
                } else {
                    binding.tvClockIn.text = "-"
                }
            }
        }

        checkStatusViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(requireContext(), errorMessage.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        checkStatusViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                binding.layoutTop.visibility = View.GONE
                binding.recyclerViewAttendance.visibility = View.GONE
                binding.shimmerGetCheckLayout.startShimmer()
                binding.shimmerGetCheckLayout.visibility = View.VISIBLE
            } else {
                lifecycleScope.launch {
                    delay(1000)
                    binding.shimmerGetCheckLayout.stopShimmer()
                    binding.shimmerGetCheckLayout.visibility = View.GONE
                    binding.layoutTop.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun setupSpinner() {
        val currentMonth = SimpleDateFormat("MMMM", Locale.US).format(System.currentTimeMillis())
        binding.powerSpinnerView.apply {
            setItems(months)
            spinnerPopupBackground = resources.getDrawable(R.drawable.spinner_background)
            selectItemByIndex(months.indexOf(currentMonth))
            setOnSpinnerItemSelectedListener<String> { _, _, _, selectedItem ->
                observeAttendanceData(selectedItem)
            }
            observeAttendanceData(currentMonth)
            setOnSpinnerOutsideTouchListener { _, _ ->
                binding.powerSpinnerView.dismiss()
            }
        }
    }

    private fun getHourFromDateString(dateString: String): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return try {
            val date = format.parse(dateString)
            val hourFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            hourFormat.format(date!!)
        } catch (e: Exception) {
            e.printStackTrace()
            "Error parsing date"
        }
    }

    private fun changeHourAmPmFormat(dateTimeString: String): String {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return try {
            val date = format.parse(dateTimeString)
            val hourFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            hourFormat.format(date!!)
        } catch (e: Exception) {
            e.printStackTrace()
            "Error parsing date"
        }
    }

    @SuppressLint("NewApi")
    private fun calculateTotalHoursToday(inTime: String, outTime: String): String {
        val timeIn = LocalTime.parse(inTime)
        val timeOut = LocalTime.parse(outTime)
        val duration = Duration.between(timeIn, timeOut)
        val hours = duration.toHours()
        val minutes = duration.toMinutes() % 60

        return String.format("$hours hr $minutes min")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        binding.powerSpinnerView.dismiss()
    }
}
