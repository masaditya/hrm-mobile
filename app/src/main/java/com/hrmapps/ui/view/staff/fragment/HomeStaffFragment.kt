package com.hrmapps.ui.view.staff.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieCompositionFactory
import com.hrmapps.R
import com.hrmapps.data.api.RetrofitBuilder
import com.hrmapps.data.repository.attendance.CheckInStatusRepository
import com.hrmapps.data.repository.attendance.CheckOutRepository
import com.hrmapps.databinding.FragmentHomeStaffBinding
import com.hrmapps.ui.view.personil.activity.CameraCheckInActivity
import com.hrmapps.ui.view.staff.activity.LeaveStaffActivity
import com.hrmapps.ui.view.staff.activity.TimeSheetActivity
import com.hrmapps.ui.viewmodel.attendance.CheckInStatusViewModel
import com.hrmapps.ui.viewmodel.attendance.CheckInStatusViewModelFactory
import com.hrmapps.ui.viewmodel.attendance.CheckOutViewModel
import com.hrmapps.ui.viewmodel.attendance.CheckOutViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalTime
import java.util.Locale

class HomeStaffFragment : Fragment() {
    private var _binding: FragmentHomeStaffBinding? = null
    private val binding get() = _binding!!
    private lateinit var checkStatusViewModel: CheckInStatusViewModel
    private lateinit var checkOutViewModel: CheckOutViewModel
    private var setCheckIn: String = ""
    private var idCheckIn: Int = 0
    private lateinit var sharedPreferences: SharedPreferences
    private val handler = Handler()
    private var token: String = ""
    private var userId: Int = 0
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
        _binding = FragmentHomeStaffBinding.inflate(inflater, container, false)
        sharedPreferences =
            requireContext().getSharedPreferences("userPref", MODE_PRIVATE)
        setupUI()
        setupViewModel()
        observeCheckStatus()
        observeCheckOut()

        if (binding.tvCheckOut.text.isNotEmpty()){
            observeCheckStatus()
        }

        handler.post(runnable)
        return binding.root
    }

    private fun setupViewModel() {
        val apiService = RetrofitBuilder.apiService
        val repositoryCheckIn = CheckInStatusRepository(apiService)
        val repositoryCheckOut = CheckOutRepository(apiService)

        val checkStatusViewModelFactory = CheckInStatusViewModelFactory(repositoryCheckIn)
        val checkOutViewModelFactory = CheckOutViewModelFactory(repositoryCheckOut)
        checkStatusViewModel =
            ViewModelProvider(this, checkStatusViewModelFactory)[CheckInStatusViewModel::class.java]
        checkOutViewModel = ViewModelProvider(this, checkOutViewModelFactory)[CheckOutViewModel::class.java]

        token = sharedPreferences.getString("token", "").toString()
        userId = sharedPreferences.getInt("userId", 0)
        token.let { checkStatusViewModel.getCheckInStatus(it, userId) }
        observeCheckStatus()
    }

    private fun setupUI() {
        binding.btPresent.setOnClickListener {
            if (binding.tvPresent.text == "Check Out") {
                dialogCheckOut()
                observeCheckStatus()
            } else {
                val intent = Intent(requireContext(), CameraCheckInActivity::class.java)
                startActivity(intent)
            }
        }

        binding.btLeave.setOnClickListener {
            startActivity(Intent(requireContext(), LeaveStaffActivity::class.java))
        }
        binding.btEForm.setOnClickListener {
            val intent = Intent(requireContext(), TimeSheetActivity::class.java)
            intent.putExtra("layout", "staff")
            startActivity(intent)
        }
    }

    private fun observeCheckStatus() {
        checkStatusViewModel.checkInStatus.observe(viewLifecycleOwner) { response ->
            if (response?.data != null) {
                idCheckIn = response.data.id
                if (response.data.clock_in_time != null){
                    binding.tvPresent.text = "Check Out"
                    val clockCheckIn = response.data.clock_in_time
                    val clockInTime = getHourFromDateString(clockCheckIn)
                    binding.tvCheckIn.text = "Check In : $clockInTime"
                    if (response.data.clock_out_time != null){
                        binding.tvPresent.text = "Check In"
                        val clockCheckOut = response.data.clock_out_time
                        val clockOutTime = getHourFromDateString(clockCheckOut)
                        binding.tvCheckOut.text = "Check Out : $clockOutTime"
                        binding.linearLayout4.visibility = View.VISIBLE
                        binding.tvLengthOfWork.text = calculateTotalHours(clockInTime, clockOutTime)
                    }
                }else{
                    binding.tvPresent.text = "Check In"
                }
            } else {
                Log.d("HomeFragment", "Response or data is null. Set Check In: $setCheckIn")
            }
        }
        checkStatusViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()){
                Toast.makeText(requireContext(), errorMessage.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        checkStatusViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.shimmerLayout.startShimmer()
                binding.layout.visibility = View.GONE
                binding.shimmerLayout.visibility = View.VISIBLE
            } else {
                lifecycleScope.launch {
                    delay(1000)
                    binding.shimmerLayout.stopShimmer()
                    binding.layout.visibility = View.VISIBLE
                    binding.shimmerLayout.visibility = View.GONE
                }

            }
        }
    }

    @SuppressLint("NewApi")
    private fun observeCheckOut() {
        checkOutViewModel.checkOutResponse.observe(viewLifecycleOwner){ response ->
            if (response != null) {
                showCheckOutResultDialog(true)
                val clockCheckOut = response.data.clock_out_time
                val clockOutTime = getHourFromDateString(clockCheckOut)
                binding.tvCheckOut.text = "Check Out : $clockOutTime"
                observeCheckStatus()
            } else {
                Toast.makeText(requireContext(), "Check-out failed", Toast.LENGTH_SHORT).show()
                showCheckOutResultDialog(false)
            }
        }

        checkOutViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage.toString(), Toast.LENGTH_SHORT).show()
            Log.d("HomeFragment", "Error message: $errorMessage")
        }
        checkOutViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                binding.shimmerLayout.startShimmer()
                binding.layout.visibility = View.GONE
                binding.shimmerLayout.visibility = View.VISIBLE
            } else {
                lifecycleScope.launch {
                    delay(1000)
                    binding.shimmerLayout.stopShimmer()
                    binding.layout.visibility = View.VISIBLE
                    binding.shimmerLayout.visibility = View.GONE
                }

            }
        })


    }

    private fun updateTimeAndDate() {
        val calendar = Calendar.getInstance()
        val hours = calendar.get(Calendar.HOUR_OF_DAY)
        val minutes = calendar.get(Calendar.MINUTE)
        val seconds = calendar.get(Calendar.SECOND)

        val dateFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
        val date = dateFormat.format(calendar.time)

        binding.tvHours.text = String.format("%02d", hours)
        binding.tvMinutes.text = String.format("%02d", minutes)
        binding.tvSecond.text = String.format("%02d", seconds)
        binding.tvDate.text = date
    }

    private fun dialogCheckOut() {
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Check Out")
        builder.setMessage("Are you sure you want to check out?")
        builder.setPositiveButton("Yes") { dialog, which ->
            val token = sharedPreferences.getString("token", "")
            val userId = sharedPreferences.getInt("userId", 0)
            val clockOutTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                Calendar.getInstance().time)
            val autoClockOut = "0"
            val clockOutIp = "182.1.120.208"
            val halfDay = "no"
            lifecycleScope.launch {
                token?.let {
                    checkOutViewModel.checkOut(
                        token = it,
                        id = idCheckIn,
                        userId = userId,
                        clockOutTime = clockOutTime,
                        autoClockOut = autoClockOut,
                        clockOutIp = clockOutIp,
                        halfDay = halfDay
                    )
                }

            }

        }
        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
    private fun showCheckOutResultDialog(isSuccess: Boolean) {
        val dialog = Dialog(requireContext())
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_checkout_result, null)
        dialog.setContentView(dialogView)

        val ivStatusIcon = dialogView.findViewById<LottieAnimationView>(R.id.ivStatusIcon)
        val tvStatusMessage = dialogView.findViewById<TextView>(R.id.tvStatusMessage)
        val tvDetailedMessage = dialogView.findViewById<TextView>(R.id.tvDetailedMessage)
        val tvClose = dialogView.findViewById<TextView>(R.id.tvClose)

        if (isSuccess) {
            binding.tvPresent.text = "Check In"
            LottieCompositionFactory.fromRawRes(requireContext(), R.raw.success).addListener { composition ->
                ivStatusIcon.setComposition(composition)
                ivStatusIcon.playAnimation()
            }.addFailureListener { e ->
                Log.e("HomeFragment", "Error loading animation: ${e.message}")
            }
            tvStatusMessage.text = "Check-out Berhasil!"
            binding.linearLayout4.visibility = View.VISIBLE
            tvStatusMessage.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
            tvDetailedMessage.text = "Terima kasih atas dedikasi dan kerja keras Anda hari ini. Semoga istirahat Anda menyenangkan!"
            observeCheckStatus()
        } else {
            LottieCompositionFactory.fromRawRes(requireContext(), R.raw.failed).addListener { composition ->
                ivStatusIcon.setComposition(composition)
                ivStatusIcon.playAnimation()
            }.addFailureListener { e ->
                Log.e("HomeFragment", "Error loading animation: ${e.message}")

            }
            tvStatusMessage.text = "Check-out Gagal"
            tvStatusMessage.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            tvDetailedMessage.text = "Terjadi masalah saat check-out. Mohon coba lagi nanti atau hubungi tim IT untuk bantuan."
        }


        dialog.show()
        var countdown = 3
        val handler = Handler(Looper.getMainLooper())
        val countdownRunnable = object : Runnable {
            override fun run() {
                tvClose.text = "Menutup otomatis dalam $countdown detik"
                dialog.setCancelable(false)
                if (countdown > 0) {
                    countdown--
                    handler.postDelayed(this, 1000)
                } else {
                    dialog.dismiss()
                    lifecycleScope.launch {
                        token.let { checkStatusViewModel.getCheckInStatus(it, userId) }
                    }
                }
            }
        }
        handler.post(countdownRunnable)
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

    @SuppressLint("NewApi")
    private fun calculateTotalHours(inTime: String, outTime: String): String {
        val timeIn = LocalTime.parse(inTime)
        val timeOut = LocalTime.parse(outTime)
        val duration = Duration.between(timeIn, timeOut)
        val hours = duration.toHours()
        val minutes = duration.toMinutes() % 60

        return String.format("$hours hr $minutes min")
    }

    override fun onResume() {
        super.onResume()
        observeCheckStatus()
        handler.post(runnable)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(runnable)
        _binding = null
    }
}