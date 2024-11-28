package com.hrmapps.ui.view.activity

import android.Manifest
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hrmapps.R
import com.hrmapps.databinding.ActivityCameraPatrolBinding
import java.io.File
import java.io.FileOutputStream

class CameraPatrolActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraPatrolBinding
    private val CAMERA_PERMISSION_CODE = 100
    private var isFrontCamera = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCameraPatrolBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        checkCameraPermission()
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.captureButton.setOnClickListener {
            captureImage()
        }

        binding.switchCameraButton.setOnClickListener {
            toggleCamera()
        }

        binding.toolbar.title = "Check-In Selfie"
        startCamera()
    }

    @OptIn(ExperimentalGetImage::class)
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = binding.previewView.surfaceProvider
            }

            val cameraSelector = if (isFrontCamera) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this, cameraSelector, preview
            )
        }, ContextCompat.getMainExecutor(this))
    }

    private fun toggleCamera() {
        val rotateAnimator = ObjectAnimator.ofFloat(binding.switchCameraButton, "rotation", 0f, 360f)
        rotateAnimator.duration = 500
        rotateAnimator.interpolator = AccelerateDecelerateInterpolator()
        rotateAnimator.start()
        binding.previewView.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                isFrontCamera = !isFrontCamera
                startCamera()

                binding.previewView.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start()
            }
            .start()
    }


    private fun saveBitmapToFile(bitmap: Bitmap): Uri? {
        val file = File(cacheDir, "captured_image.png")
        return try {
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.flush()
                Uri.fromFile(file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun captureImage() {
        binding.previewView.bitmap?.let { bitmap ->
            val imageUri = saveBitmapToFile(bitmap)
            if (imageUri != null) {
                val intent = Intent(this, PresentCheckInActivity::class.java)
                intent.putExtra("captured_image_uri", imageUri.toString())
                startActivity(intent)
                finish()
            }
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            Log.d("Permission", "Izin kamera diberikan")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission", "Izin kamera diberikan")
            } else {
                Toast.makeText(this, "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
            }
        }
    }
}