package com.hrmapps.ui.view.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
import com.hrmapps.databinding.ActivityPresentBinding
import kotlin.math.pow

class PresentActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityPresentBinding
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var officeLocation: LatLng
    private val LOCATION_PERMISSION_REQUEST_CODE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPresentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_IMAGE_CAPTURE)
        } else {
            openCamera()
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            checkUserLocation()
        }

        binding.reSelfie.setOnClickListener {
            openCamera()
        }
        binding.mToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        officeLocation = LatLng(-7.380462720372436, 112.72159771226829)  // Sesuaikan koordinat kantor
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

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } else {
            Log.d("Camera", "No camera app found")
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

    private fun checkUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val userLatLng = LatLng(it.latitude, it.longitude)
                val distanceToOffice = calculateDistance(userLatLng, officeLocation)

                if (distanceToOffice <= 100) {
                    binding.btnCheckIn.isEnabled = true
                    Snackbar.make(findViewById(R.id.main), "You are within 100 meters!", Snackbar.LENGTH_LONG).show()
                } else {
                    binding.btnCheckIn.isEnabled = false
                    Snackbar.make(findViewById(R.id.main), "You are outside the 100 meter range!", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun calculateDistance(startP: LatLng, endP: LatLng): Double {
        val radiusOfEarth = 6371000.0  // Radius bumi dalam meter
        val latDistance = Math.toRadians(endP.latitude - startP.latitude)
        val lngDistance = Math.toRadians(endP.longitude - startP.longitude)
        val a = Math.sin(latDistance / 2).pow(2.0) +
                Math.cos(Math.toRadians(startP.latitude)) * Math.cos(Math.toRadians(endP.latitude)) *
                Math.sin(lngDistance / 2).pow(2.0)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
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
