package com.hrmapps.ui.view.personil.activity

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieCompositionFactory
import com.hrmapps.R
import com.hrmapps.data.api.RetrofitBuilder
import com.hrmapps.data.model.leave.LeaveTypes
import com.hrmapps.data.repository.leave.CreateLeaveRepository
import com.hrmapps.data.repository.leave.LeaveTypesRepository
import com.hrmapps.databinding.ActivityAddNewFormLeaveBinding
import com.hrmapps.ui.viewmodel.leave.LeaveTypesViewModel
import com.hrmapps.ui.viewmodel.leave.LeaveTypesViewModelFactory
import com.hrmapps.ui.viewmodel.leave.CreateLeaveViewModel
import com.hrmapps.ui.viewmodel.leave.CreateLeaveViewModelFactory
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddNewFormLeaveActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNewFormLeaveBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var typesLeaveViewModel: LeaveTypesViewModel
    private lateinit var createLeaveViewModel: CreateLeaveViewModel
    private lateinit var token: String
    private var userId: Int = 0
    private var companyId: Int = 0
    private var typeId: Int = 0
    private lateinit var selectedItem: String
    private var selectedFileUri: Uri? = null

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                selectedFileUri = result.data?.data
                getBitmapFromUri(selectedFileUri.toString())
                binding.textViewFileName.text = selectedFileUri?.path
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewFormLeaveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupListeners()
        setupViewModel()
        observeLeaveTypes()
        observeLeaveSubmission()
        checkPermissions()
    }

    private fun setupUI() {
        sharedPreferences = getSharedPreferences("isLoggedIn", MODE_PRIVATE)
        token = sharedPreferences.getString("token", "") ?: ""
        companyId = sharedPreferences.getInt("companyId", 0)
        userId = sharedPreferences.getInt("userId", 0)
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                101
            )
        }
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                102
            )
        }
    }

    private fun setupListeners() {
        binding.mToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.powerSpinnerView2.setOnSpinnerItemSelectedListener<String> { _, _, _, item ->
            selectedItem = item
            typeId = typesLeaveViewModel.leaveTypes.value?.data?.find { it.type_name == item }?.id ?: 0
        }

        binding.editTextStartDate.setOnClickListener {
            showDatePicker { selectedDate ->
                binding.editTextStartDate.setText(selectedDate)
            }
        }

        binding.editTextEndDate.setOnClickListener {
            showDatePicker { selectedDate ->
                binding.editTextEndDate.setText(selectedDate)
            }
        }

        binding.buttonSelectFile.setOnClickListener {
            openGallery()
        }

        binding.buttonSubmit.setOnClickListener {
            validateAndSubmitLeave()
        }
    }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun setupViewModel() {
        val apiService = RetrofitBuilder.apiService
        val typeRepository = LeaveTypesRepository(apiService)
        val typeViewModelFactory = LeaveTypesViewModelFactory(typeRepository)

        val createLeaveRepository = CreateLeaveRepository(apiService)
        val createLeaveViewModelFactory = CreateLeaveViewModelFactory(createLeaveRepository)

        typesLeaveViewModel =
            ViewModelProvider(this, typeViewModelFactory)[LeaveTypesViewModel::class.java]
        createLeaveViewModel = ViewModelProvider(this, createLeaveViewModelFactory)[CreateLeaveViewModel::class.java]
    }

    private fun observeLeaveTypes() {
        typesLeaveViewModel.getLeaveTypes(token)
        typesLeaveViewModel.leaveTypes.observe(this) { response ->
            val leaveTypes = response.data.map { LeaveTypes(it.id, it.type_name) }
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                leaveTypes.map { it.type_name })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.powerSpinnerView2.setItems(leaveTypes.map { it.type_name })
        }
        typesLeaveViewModel.errorMessage.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }
        typesLeaveViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.loadingBar.visibility = View.VISIBLE
                binding.buttonSubmit.isEnabled = false
            } else {
                binding.loadingBar.visibility = View.GONE
                binding.buttonSubmit.isEnabled = true
            }
        }
    }

    private fun observeLeaveSubmission() {
        createLeaveViewModel.createLeaveResponseLiveData.observe(this) { response ->
            if (response != null) {
                showLeaveDialog(true, "Success", "Leave request submitted successfully")
            }
        }

        createLeaveViewModel.errorMessageLiveData.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                showLeaveDialog(false, "Error", errorMessage)
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        createLeaveViewModel.isLoadingLiveData.observe(this) { isLoading ->
            if (isLoading) {
                binding.loadingBar.visibility = View.VISIBLE
                binding.buttonSubmit.isEnabled = false
            } else {
                binding.loadingBar.visibility = View.GONE
                binding.buttonSubmit.isEnabled = true
            }
        }
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val currentDate = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val date = "$year-${month + 1}-$dayOfMonth"
                onDateSelected(date)
            },
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun validateAndSubmitLeave() {
        val leaveType = binding.powerSpinnerView2.text.toString()
        val startDate = binding.editTextStartDate.text.toString()
        val endDate = binding.editTextEndDate.text.toString()
        val leaveReason = binding.editTextLeaveReason.text.toString()

        when {

            leaveType.isEmpty() -> {
                Toast.makeText(this, "Please select a leave type", Toast.LENGTH_SHORT).show()
                return
            }

            startDate.isEmpty() || endDate.isEmpty() -> {
                Toast.makeText(this, "Please select start and end dates", Toast.LENGTH_SHORT).show()
                return
            }

            !isValidDateRange(startDate, endDate) -> {
                Toast.makeText(this, "Invalid date range", Toast.LENGTH_SHORT).show()
                return
            }

            leaveReason.isEmpty() -> {
                Toast.makeText(this, "Please provide a leave reason", Toast.LENGTH_SHORT).show()
                return
            }

            else -> submitLeaveRequest()
        }
    }

    private fun isValidDateRange(startDateStr: String, endDateStr: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return try {
            val startDate = dateFormat.parse(startDateStr)
            val endDate = dateFormat.parse(endDateStr)
            startDate != null && endDate != null && !startDate.after(endDate)
        } catch (e: Exception) {
            false
        }
    }

    private fun submitLeaveRequest() {
        val startDate = binding.editTextStartDate.text.toString()
        val endDate = binding.editTextEndDate.text.toString()
        val leaveReason = binding.editTextLeaveReason.text.toString()

        val photo = binding.ivPreview.drawable?.toBitmap()?.let { bitmapToFile(it) }

        createLeaveViewModel.createLeave(
            token,
            companyId.toString(),
            userId.toString(),
            typeId.toString(),
            startDate,
            endDate,
            leaveReason,
            userId.toString(),
            photo
        )

    }
    private fun bitmapToFile(bitmap: Bitmap): File? {
        val file = File(cacheDir, "${System.currentTimeMillis()}.jpg").apply { createNewFile() }
        var quality = 100
        var sizeInBytes: Long

        do {
            FileOutputStream(file).use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, it)
            }
            sizeInBytes = file.length()
            quality -= 10
        } while (sizeInBytes > 1 * 1024 * 1024 && quality > 0)

        return if (sizeInBytes <= 1 * 1024 * 1024) file else null.also {
            Log.e("BitmapError", "Bitmap too large.")
        }
    }
    private fun getBitmapFromUri(uriString: String) {
        val uri = Uri.parse(uriString)
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        binding.ivPreview.setImageBitmap(bitmap)
    }


    private fun showLeaveDialog(isSuccess: Boolean, title: String, message: String) {
        val dialog = Dialog(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_checkin_result, null)
        dialog.setContentView(dialogView)

        val ivStatusIcon = dialogView.findViewById<LottieAnimationView>(R.id.ivStatusIcon)
        val tvStatusMessage = dialogView.findViewById<TextView>(R.id.tvStatusMessage)
        val tvDetailedMessage = dialogView.findViewById<TextView>(R.id.tvDetailedMessage)
        val tvClose = dialogView.findViewById<TextView>(R.id.tvClose)

        if (isSuccess) {
            LottieCompositionFactory.fromRawRes(this, R.raw.success).addListener { composition ->
                ivStatusIcon.setComposition(composition)
                ivStatusIcon.playAnimation()
            }
            tvStatusMessage.text = title
            tvStatusMessage.setTextColor(ContextCompat.getColor(this, R.color.green))
            tvDetailedMessage.text = message
        } else {
            LottieCompositionFactory.fromRawRes(this, R.raw.failed).addListener { composition ->
                ivStatusIcon.setComposition(composition)
                ivStatusIcon.playAnimation()
            }
            tvStatusMessage.text = title
            tvStatusMessage.setTextColor(ContextCompat.getColor(this, R.color.red))
            tvDetailedMessage.text = message
        }

        dialog.show()
        var countdown = 3
        val handler = Handler(Looper.getMainLooper())
        val countdownRunnable = object : Runnable {
            override fun run() {
                tvClose.text = "Menutup otomatis dalam $countdown detik"
                if (countdown > 0) {
                    countdown--
                    handler.postDelayed(this, 1000)
                } else {
                    dialog.dismiss()
                    if (isSuccess) {
                       finish()
                    }
                }
            }
        }
        handler.post(countdownRunnable)
    }

}