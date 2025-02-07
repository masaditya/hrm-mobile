package com.hrmpandjiadhi.ui.view.personil.activity

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.hrmpandjiadhi.R
import com.hrmpandjiadhi.data.api.RetrofitBuilder
import com.hrmpandjiadhi.data.repository.attendance.CheckInRepository
import com.hrmpandjiadhi.data.repository.auth.LocationOfficeUserRepository
import com.hrmpandjiadhi.databinding.ActivityPresentCheckInBinding
import com.hrmpandjiadhi.ui.viewmodel.attendance.CheckInViewModel
import com.hrmpandjiadhi.ui.viewmodel.attendance.CheckInViewModelFactory
import com.hrmpandjiadhi.ui.viewmodel.auth.LocationOfficeUserViewModel
import com.hrmpandjiadhi.ui.viewmodel.auth.LocationOfficeUserViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.*

@Suppress("DEPRECATION")
class PresentCheckInActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityPresentCheckInBinding
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var viewModel: CheckInViewModel
    private lateinit var getLocationOfficeUserViewModel: LocationOfficeUserViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var officeLocationUser: LatLng
    private var locationId: String = ""
    private var workFromType: String = ""
    private var token: String = ""
    private lateinit var latitude: String
    private lateinit var longitude: String
    private val LOCATION_PERMISSION_REQUEST_CODE = 2
    private var distanceToOffice: Double = 0.0
    private var isRadiusActive: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setupView()
        initViewModels()
        setupListeners()
        checkLocationPermission()
        CoroutineScope(Dispatchers.Main).launch { getCurrentLocation() }
        getLocationOfficeUser()
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
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
        mMap.isMyLocationEnabled = true



    }
    private fun setupView() {
        enableEdgeToEdge()
        binding = ActivityPresentCheckInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupWindowInsets()
        binding.mapView.onCreate(null)
        binding.mapView.getMapAsync(this)
        sharedPreferences = getSharedPreferences("userPref", MODE_PRIVATE)
        token = sharedPreferences.getString("token", "") ?: ""
        val imgUri = intent.getStringExtra("captured_image_uri")
        if (imgUri != null) {
            getBitmapFromUri(imgUri)
        }
    }

    private fun initViewModels() {
        val apiService = RetrofitBuilder.apiService
        viewModel = ViewModelProvider(
            this,
            CheckInViewModelFactory(CheckInRepository(apiService))
        )[CheckInViewModel::class.java]

        getLocationOfficeUserViewModel = ViewModelProvider(
            this,
            LocationOfficeUserViewModelFactory(LocationOfficeUserRepository(apiService))
        )[LocationOfficeUserViewModel::class.java]

    }

    private fun setupListeners() {
        binding.reSelfie.setOnClickListener {
            val intent = Intent(this, CameraCheckInActivity::class.java)
            intent.putExtra("Page", "Check-In Selfie")
            startActivity(intent)
        }

        binding.mToolbar.setNavigationOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }

        binding.powerSpinnerView2.setItems(resources.getStringArray(R.array.working_locations).toList())
        binding.powerSpinnerView2.setOnSpinnerItemSelectedListener<String> { _, _, _, newItem ->
            workFromType = newItem
            if (newItem == "office"){
                isRadiusActive
            } else {
                !isRadiusActive
            }
        }

        binding.btnCheckIn.setOnClickListener {
            if (isRadiusActive){
                if (distanceToOffice <= 100) {
                    Snackbar.make(findViewById(R.id.main), "You are within 100 meters!", Snackbar.LENGTH_LONG).show()
                    observeViewModel()
                    handleCheckIn()
                } else {
                    Snackbar.make(findViewById(R.id.main), "You are outside the 100 meter range!", Snackbar.LENGTH_LONG).show()
                }
            }else{
                observeViewModel()
                handleCheckIn()
            }

        }
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun handleCheckIn() {
        val clockInTime = getCurrentDateTimeFormatted()
        val photo = binding.imgPreview.drawable?.toBitmap()?.let { bitmapToFile(it) }

        if (photo == null) {
            Toast.makeText(this, "Please take a selfie", Toast.LENGTH_SHORT).show()
            return
        }
        if (workFromType.isEmpty()) {
            Toast.makeText(this, "Please select work from type", Toast.LENGTH_SHORT).show()
            return
        }

        val companyId = sharedPreferences.getInt("companyId", 0).toString()
        val userId = sharedPreferences.getInt("userId", 0).toString()
        val clockInIp = "182.1.120.208"

        viewModel.checkIn(
            companyId,
            userId,
            locationId,
            clockInTime,
            "0",
            clockInIp,
            "no",
            latitude,
            longitude,
            workFromType,
            "yes",
            photo,
            token
        )
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
                setAddressFromLocation(it.latitude, it.longitude)
            }
        }
    }

    private fun setAddressFromLocation(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            val address = addresses?.firstOrNull()?.getAddressLine(0) ?: "Unable to get address"
            binding.etAddress.setText(address)
        } catch (e: IOException) {
            binding.etAddress.setText("Geocoder failed")
            Toast.makeText(this, "Geocoder failed", Toast.LENGTH_SHORT).show()
            Log.e("GeocoderError", e.message.toString())
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
    private fun getCurrentDateTimeFormatted(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun getLocationOfficeUser() {
        getLocationOfficeUserViewModel.getLocationOfficeUser(token)
        getLocationOfficeUserViewModel.locationOfficeUser.observe(this) { response ->
            response?.data?.let {
                officeLocationUser = LatLng(it.latitude.toDouble(), it.longitude.toDouble())
                locationId = it.company_address_id.toString()
                mMap.addMarker(MarkerOptions().position(officeLocationUser).title("Office Location"))

                mMap.addCircle(
                    CircleOptions()
                        .center(officeLocationUser)
                        .radius(100.0)
                        .strokeColor(ContextCompat.getColor(this, R.color.primary))
                        .fillColor(ContextCompat.getColor(this, R.color.primary_transparent))
                )
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(officeLocationUser, 100f))
                checkUserLocation()
            }
        }
        getLocationOfficeUserViewModel.errorMessage.observe(this) {errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(this, errorMessage.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.checkInResponse.observe(this) { response ->
            showCheckInResultDialog(response != null)
            if (response != null) saveCheckInId(response.data.id)
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(this, errorMessage.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.loadingBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun saveCheckInId(id: Int) {
        sharedPreferences.edit().putInt("idCheckIn", id).apply()
    }

    private fun showCheckInResultDialog(isSuccess: Boolean) {
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
            tvStatusMessage.text = "Check-In Berhasil!"
            tvStatusMessage.setTextColor(ContextCompat.getColor(this, R.color.green))
            tvDetailedMessage.text = "Terima kasih telah melakukan check-in hari ini! Semoga hari Anda menyenangkan!"
        } else {
            LottieCompositionFactory.fromRawRes(this, R.raw.failed).addListener { composition ->
                ivStatusIcon.setComposition(composition)
                ivStatusIcon.playAnimation()
            }.addFailureListener { e ->
                Log.e("HomeFragment", "Error loading animation: ${e.message}")
            }
            tvStatusMessage.text = "Check-In Gagal"
            tvStatusMessage.setTextColor(ContextCompat.getColor(this, R.color.red))
            tvDetailedMessage.text = "Terjadi masalah saat melakukan check-in. Mohon coba lagi nanti atau hubungi tim IT untuk bantuan."
        }

        dialog.show()
        var countdown = 3
        val handler = Handler(Looper.getMainLooper())
        val countdownRunnable = object : Runnable {
            override fun run() {
                dialog.setCancelable(false)
                tvClose.text = "Menutup otomatis dalam $countdown detik"
                if (countdown > 0) {
                    countdown--
                    handler.postDelayed(this, 1000)
                } else {
                    dialog.dismiss()
                    startActivity(Intent(this@PresentCheckInActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
        handler.post(countdownRunnable)
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


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation()
        } else {
            Snackbar.make(
                binding.root,
                "Location permission denied",
                Snackbar.LENGTH_INDEFINITE
            ).setAction("Grant") { checkLocationPermission() }.show()
        }
    }
    private fun checkUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val userLatLng = LatLng(it.latitude, it.longitude)
                distanceToOffice = calculateDistance(userLatLng, officeLocationUser)
                mMap.addMarker(MarkerOptions().position(userLatLng).title("Your Location"))

            }
        }
    }

    private fun calculateDistance(startP: LatLng, endP: LatLng): Double {
        val radiusOfEarth = 6371000.0
        val latDistance = Math.toRadians(endP.latitude - startP.latitude)
        val lngDistance = Math.toRadians(endP.longitude - startP.longitude)
        val a = sin(latDistance / 2).pow(2.0) +
                cos(Math.toRadians(startP.latitude)) * Math.cos(Math.toRadians(endP.latitude)) *
                sin(lngDistance / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return radiusOfEarth * c
    }


    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }
}
