package com.hrmapps.ui.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.hrmapps.R
import com.hrmapps.databinding.ActivityCameraBinding
import com.hrmapps.ui.components.FaceContourDetectionProcessor
import java.io.File
import java.io.FileOutputStream

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private var isFaceDetected = false
    private lateinit var faceContourProcessor: FaceContourDetectionProcessor
    private var page: String? = null

    private val CAMERA_PERMISSION_CODE = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
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

        binding.captureButton.isEnabled = false
        binding.captureButton.setOnClickListener {
            captureImage()
        }
        page = intent.getStringExtra("Page")

        binding.toolbar.title = page
        faceContourProcessor = FaceContourDetectionProcessor(binding.faceOverlayView)
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

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { analyzer ->
                    analyzer.setAnalyzer(ContextCompat.getMainExecutor(this)) { imageProxy ->
                        analyzeImage(imageProxy)
                    }
                }

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageAnalysis
            )
        }, ContextCompat.getMainExecutor(this))
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun analyzeImage(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees

        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, rotationDegrees)
            faceContourProcessor.detectInImage(image)
                .addOnSuccessListener { faces ->
                    updateFaceDetectionStatus(faces)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

    private fun updateFaceDetectionStatus(faces: List<Face>) {
        if (faces.isNotEmpty()) {
            isFaceDetected = true
            binding.captureButton.isEnabled = true
            binding.faceWarningText.visibility = TextView.GONE

        } else {
            isFaceDetected = false
            binding.captureButton.isEnabled = false
            binding.faceWarningText.visibility = TextView.VISIBLE
        }

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
                if (page == "Check-In Selfie"){
                    val intent = Intent(this, PresentCheckInActivity::class.java)
                    intent.putExtra("captured_image_uri", imageUri.toString())
                    startActivity(intent)
                    finish()
                }
                else if (page == "Patroli Selfie") {
                    val intent = Intent(this, PatrolActivity::class.java)
                    intent.putExtra("captured_image_uri", imageUri.toString())
                    startActivity(intent)
                    finish()
                }

            } else {
                // Handle error saving the image
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

    override fun onStop() {
        super.onStop()
        faceContourProcessor.stop()
    }
}
