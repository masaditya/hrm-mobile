package com.hrmapps.ui.view.personil.activity

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieCompositionFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.hrmapps.R
import com.hrmapps.data.api.RetrofitBuilder
import com.hrmapps.data.model.patroli.CheckPoint
import com.hrmapps.data.repository.patroli.CreatePatrolRepository
import com.hrmapps.data.repository.patroli.GetPatrolTypesRepository
import com.hrmapps.data.repository.auth.GetUserRepository
import com.hrmapps.databinding.ActivityPatrolBinding
import com.hrmapps.ui.viewmodel.patroli.CreatePatrolViewModel
import com.hrmapps.ui.viewmodel.patroli.CreatePatrolViewModelFactory
import com.hrmapps.ui.viewmodel.patroli.GetPatrolViewModel
import com.hrmapps.ui.viewmodel.patroli.GetPatrolViewModelFactory
import com.hrmapps.ui.viewmodel.auth.GetUserLoginViewModel
import com.hrmapps.ui.viewmodel.auth.GetUserLoginViewModelFactory
import java.io.File
import java.io.FileOutputStream

class PatrolActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPatrolBinding
    private lateinit var viewModel: CreatePatrolViewModel
    private lateinit var getUserLoginViewModel: GetUserLoginViewModel
    private lateinit var patrolViewModel: GetPatrolViewModel
    private lateinit var patrolTypesAdapter: ArrayAdapter<String>
    private lateinit var patrolTypes: List<CheckPoint>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var token: String
    private var userId: Int = 0
    private lateinit var name: String
    private var patrolTypesId: Int = 0
    private lateinit var selectedItem: String
    private lateinit var latitude: String
    private lateinit var longitude: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPatrolBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupUI()
        setupViewModel()
        checkLocationPermission()
        getCurrentLocation()
        setupViewModel()
        observePatrolTypes()
        observeUser()
        setupListeners()
    }
    private fun setupUI() {
        sharedPreferences = getSharedPreferences("userPref", MODE_PRIVATE)
        token = sharedPreferences.getString("token", "") ?: ""
        userId = sharedPreferences.getInt("userId", 0)
        val imgUri = intent.getStringExtra("captured_image_uri")
        if (imgUri != null) {
            getBitmapFromUri(imgUri)
        }
        binding.mToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupListeners() {
        binding.powerSpinnerView2.setOnSpinnerItemSelectedListener<String> { _, _, _, newItem ->
            selectedItem = newItem
            val selectedPatrolType = patrolTypes.find { it.name == newItem }
            patrolTypesId = selectedPatrolType?.id ?: 0
        }
        binding.btnSubmit.setOnClickListener {
            val desc = binding.etDescription.text.toString()
            val photo = binding.imgPreview.drawable?.toBitmap()?.let { bitmapToFile(it) }

            if (patrolTypesId.toString().isEmpty()){
                Toast.makeText(this, "Patrol Type is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (desc.isEmpty()){
                Toast.makeText(this, "Description is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (userId.toString().isEmpty()){
                Toast.makeText(this, "User ID is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            observePatrol(name, photo!!, desc, userId.toString())
        }
    }

    private fun setupViewModel() {
        val apiService = RetrofitBuilder.apiService
        val repository = CreatePatrolRepository(apiService)
        val viewModelFactory = CreatePatrolViewModelFactory(repository)

        val getUserRepository = GetUserRepository(apiService)
        val getUserLoginViewModelFactory = GetUserLoginViewModelFactory(getUserRepository)
        
        val getPatrolTypesRepository = GetPatrolTypesRepository(apiService)
        val getPatrolViewModelFactory = GetPatrolViewModelFactory(getPatrolTypesRepository)

        viewModel = ViewModelProvider(this, viewModelFactory)[CreatePatrolViewModel::class.java]
        getUserLoginViewModel = ViewModelProvider(this, getUserLoginViewModelFactory)[GetUserLoginViewModel::class.java]
        patrolViewModel = ViewModelProvider(this, getPatrolViewModelFactory)[GetPatrolViewModel::class.java]

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }
    private fun observePatrolTypes() {
        patrolViewModel.getPatrolTypes(token)
        patrolViewModel.patrolTypes.observe(this) { response ->
            if (response != null) {
                patrolTypes = response.data.map { CheckPoint(it.id, it.name) }
            }

            patrolTypesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, patrolTypes.map { it.name })
            patrolTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.powerSpinnerView2.setItems(patrolTypes.map { it.name })
        }
        patrolViewModel.errorMessage.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(this, errorMessage.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        patrolViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.loadingBar.visibility = View.VISIBLE
            } else {
                binding.loadingBar.visibility = View.GONE
            }
        }

    }
    private fun getCurrentLocation() {
        if (!hasLocationPermission()) return

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                latitude = it.latitude.toString()
                longitude = it.longitude.toString()

            }
        }
    }
    private fun observeUser() {
        getUserLoginViewModel.getUserLogin(token)

        getUserLoginViewModel.userResponse.observe(this) { response ->
            name = response.data.name
        }

        viewModel.error.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(this, errorMessage.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.loadingBar.visibility = View.VISIBLE
            } else {
                binding.loadingBar.visibility = View.GONE
            }
        }

    }

    private fun observePatrol(name: String, photo: File, desc: String, addedBy: String) {
        if (latitude.isEmpty() || longitude.isEmpty()) {
            Toast.makeText(this, "Location is required", Toast.LENGTH_SHORT).show()
            return
        }else{
            viewModel.createPatrol(
                token,
                name,
                patrolTypesId.toString(),
                desc,
                latitude,
                longitude,
                addedBy,
                photo
            )
        }

        viewModel.createPatrolResponse.observe(this) { response ->
            showResultDialog(response != null)
        }

        viewModel.isLoading.observe(this){ isLoading ->
            if (isLoading) {
                binding.loadingBar.visibility = View.VISIBLE
                binding.btnSubmit.isEnabled = false
            } else {
                binding.loadingBar.visibility = View.GONE
                binding.btnSubmit.isEnabled = true
            }

        }
        viewModel.error.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }


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
        binding.imgPreview.setImageBitmap(bitmap)
    }

    private fun checkLocationPermission() {
        if (!hasLocationPermission()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun hasLocationPermission() = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ).all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }

    private fun showResultDialog(isSuccess: Boolean) {
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
            }.addFailureListener { e ->
                Log.e("HomeFragment", "Error loading animation: ${e.message}")
            }
            tvStatusMessage.text = "Submit Success"
            tvStatusMessage.setTextColor(ContextCompat.getColor(this, R.color.green))
            tvDetailedMessage.text = "Patrol successfully created."
        } else {
            LottieCompositionFactory.fromRawRes(this, R.raw.failed).addListener { composition ->
                ivStatusIcon.setComposition(composition)
                ivStatusIcon.playAnimation()
            }.addFailureListener { e ->
                Log.e("HomeFragment", "Error loading animation: ${e.message}")
            }
            tvStatusMessage.text = "Submit Gagal"
            tvStatusMessage.setTextColor(ContextCompat.getColor(this, R.color.red))
            tvDetailedMessage.text = "Terjadi masalah saat melakukan submit. Mohon coba lagi nanti atau hubungi tim IT untuk bantuan."
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
                    startActivity(Intent(this@PatrolActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
        handler.post(countdownRunnable)
    }

}