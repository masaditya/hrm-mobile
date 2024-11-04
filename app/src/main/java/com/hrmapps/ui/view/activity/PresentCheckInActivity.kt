package com.hrmapps.ui.view.activity

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
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
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.hrmapps.R
import com.hrmapps.data.api.RetrofitBuilder
import com.hrmapps.data.repository.CheckInRepository
import com.hrmapps.databinding.ActivityPresentCheckInBinding
import com.hrmapps.ui.viewmodel.CheckInViewModel
import com.hrmapps.ui.viewmodel.CheckInViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class PresentCheckInActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityPresentCheckInBinding
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var officeLocation: LatLng
    private val LOCATION_PERMISSION_REQUEST_CODE = 2
    private lateinit var viewModel: CheckInViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var token: String
    private lateinit var bitmap: Bitmap
    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var latitude: String
    private lateinit var longitude: String
    private var workFromType: String = ""
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPresentCheckInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val imgUri = intent.getStringExtra("captured_image_uri")
        if (imgUri != null) {
            getBitmapFromUri(imgUri)
        }
        val apiService = RetrofitBuilder.apiService
        val repository = CheckInRepository(apiService)
        val factory = CheckInViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[CheckInViewModel::class.java]

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        sharedPreferences = getSharedPreferences("isLoggedIn", MODE_PRIVATE)
        token = sharedPreferences.getString("token", "").toString()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            checkUserLocation()
        }

        binding.reSelfie.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }
        binding.mToolbar.setNavigationOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }
        binding.powerSpinnerView2.setItems(resources.getStringArray(R.array.working_locations).toList())
        binding.powerSpinnerView2.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
            workFromType = newItem
        }

        binding.btnCheckIn.setOnClickListener {
            val clockInTime = getCurrentDateTimeFormatted()

            val photo = binding.imgPreview.drawable?.toBitmap()?.let { bitmapToFile(it) }
            if (photo == null) {
                Toast.makeText(this, "Photo not found, please take a selfie", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val companyId = sharedPreferences.getInt("companyId", 0).toString()
            val userId = sharedPreferences.getInt("userId", 0).toString()

            val clockInIp = "182.1.120.208"
                if (workFromType.isEmpty()){
                    Toast.makeText(this, "Please select a work location", Toast.LENGTH_SHORT).show()
                }else{
                    viewModel.checkIn(companyId, userId, clockInTime, "0", clockInIp, "no", latitude, longitude, workFromType, "yes", photo, token)

            }

        }
        CoroutineScope(Dispatchers.Main).launch {
            getCurrentLocation()
        }

        checkUserLocation()
        observeViewModel()

    }
    private fun getCurrentLocation() {
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
                val latLng = LatLng(it.latitude, it.longitude)
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
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0].getAddressLine(0)
                binding.etAddress.setText(address)
            } else {
                binding.etAddress.setText("Unable to get address")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            binding.etAddress.setText("Geocoder failed")
        }
    }

    @Deprecated("Deprecated", ReplaceWith("Use registerForActivityResult instead"))
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.imgPreview.setImageBitmap(imageBitmap)
            binding.imgPreview.visibility = View.VISIBLE
            binding.reSelfie.visibility = View.VISIBLE
        }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        officeLocation = LatLng(-6.909369598005857, 107.1687067026662)  // Sesuaikan koordinat kantor
        mMap.addMarker(MarkerOptions().position(officeLocation).title("Office"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(officeLocation, 18f))

        val circleOptions = CircleOptions()
            .center(officeLocation)
            .radius(100.0)  // Radius dalam meter
            .strokeColor(0x220000FF)
            .fillColor(0x220000FF)
            .strokeWidth(2f)
        mMap.addCircle(circleOptions)
    }



    private fun checkUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val userLatLng = LatLng(it.latitude, it.longitude)
                val distanceToOffice = calculateDistance(userLatLng, officeLocation)

                if (distanceToOffice <= 100000000) {
                    binding.btnCheckIn.isEnabled = true
                    binding.btnCheckIn.text = "Check-in Now"
                    Snackbar.make(findViewById(R.id.main), "You are within 100 meters!", Snackbar.LENGTH_LONG).show()
                } else {
                    binding.btnCheckIn.isEnabled = false
                    binding.btnCheckIn.text = "You are outside office"
                    Snackbar.make(findViewById(R.id.main), "You are outside the 100 meter range!", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun calculateDistance(startP: LatLng, endP: LatLng): Double {
        val radiusOfEarth = 6371000.0  // Radius bumi dalam meter
        val latDistance = Math.toRadians(endP.latitude - startP.latitude)
        val lngDistance = Math.toRadians(endP.longitude - startP.longitude)
        val a = sin(latDistance / 2).pow(2.0) +
                cos(Math.toRadians(startP.latitude)) * Math.cos(Math.toRadians(endP.latitude)) *
                sin(lngDistance / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return radiusOfEarth * c
    }

    private fun observeViewModel() {
        viewModel.checkInResponse.observe(this) { response ->
            if (response != null) {
                val editor = sharedPreferences.edit()
                editor.putInt("idCheckIn", response.data.id)
                editor.apply()
                showCheckInResultDialog(true)
            } else {
                Toast.makeText(this, "Check-in failed", Toast.LENGTH_SHORT).show()
                showCheckInResultDialog(false)
            }
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            Log.e("CheckInError", errorMessage)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.loadingBar.visibility = View.VISIBLE
            } else {
                binding.loadingBar.visibility = View.GONE
            }
        }
    }
    private fun bitmapToFile(bitmap: Bitmap): File? {
        val file = File(cacheDir, "${System.currentTimeMillis()}.jpg")
        file.createNewFile()

        var quality = 100
        var outputStream: FileOutputStream? = null
        var sizeInBytes: Long

        do {
            outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.flush()
            sizeInBytes = file.length()
            outputStream.close()
            quality -= 10
        } while (sizeInBytes > 1 * 1024 * 1024 && quality > 0)

        return if (sizeInBytes <= 1 * 1024 * 1024) {
            file
        } else {
            Log.e("BitmapError", "Bitmap is too large to be compressed under 1 MB.")
            null
        }
    }

    private fun getCurrentDateTimeFormatted(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }
    private fun showCheckInResultDialog(isSuccess: Boolean) {
        val dialog = Dialog(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_checkin_result, null)
        dialog.setContentView(dialogView)

        val ivStatusIcon = dialogView.findViewById<ImageView>(R.id.ivStatusIcon)
        val tvStatusMessage = dialogView.findViewById<TextView>(R.id.tvStatusMessage)
        val tvDetailedMessage = dialogView.findViewById<TextView>(R.id.tvDetailedMessage)
        val tvClose = dialogView.findViewById<TextView>(R.id.tvClose)

        if (isSuccess) {
            ivStatusIcon.setImageResource(R.drawable.ic_success_circle)
            tvStatusMessage.text = "Check-In Berhasil!"
            tvStatusMessage.setTextColor(ContextCompat.getColor(this, R.color.green))
            tvDetailedMessage.text = "Terima kasih telah melakukan check-in hari ini! Semoga hari Anda menyenangkan!"
        } else {
            ivStatusIcon.setImageResource(R.drawable.ic_failed_circle)
            tvStatusMessage.text = "Check-In Gagal"
            tvStatusMessage.setTextColor(ContextCompat.getColor(this, R.color.red))
            tvDetailedMessage.text = "Terjadi masalah saat melakukan check-in. Mohon coba lagi nanti atau hubungi tim IT untuk bantuan."
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
                    startActivity(Intent(this@PresentCheckInActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
        handler.post(countdownRunnable)
    }

    private fun getBitmapFromUri(uriString: String) {
        val uri = Uri.parse(uriString)
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        binding.imgPreview.setImageBitmap(bitmap)
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

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkUserLocation()
        }
    }
}
