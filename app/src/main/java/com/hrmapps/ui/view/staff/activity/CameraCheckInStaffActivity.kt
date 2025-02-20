package com.hrmapps.ui.view.staff.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.hrmapps.R
import com.hrmapps.databinding.ActivityCameraCheckInStaffBinding
import com.hrmapps.ui.components.FaceContourDetectionProcessor
import java.io.File
import java.io.FileOutputStream

class CameraCheckInStaffActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraCheckInStaffBinding
    private var isFaceDetected = false
    private lateinit var faceContourProcessor: FaceContourDetectionProcessor
    private lateinit var progressDialog: ProgressDialog
    private val CAMERA_PERMISSION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCameraCheckInStaffBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

        checkCameraPermission()
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.captureButton.isEnabled = false
        binding.captureButton.setOnClickListener {
            captureImage()
        }

        binding.toolbar.title = "Check-In Selfie"
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
        binding.captureButton.isEnabled = false
        progressDialog.show()
        binding.previewView.bitmap?.let { bitmap ->
            val imageUri = saveBitmapToFile(bitmap)
            if (imageUri != null) {
                val intent = Intent(this, PresentCheckInStaffActivity::class.java)
                intent.putExtra("captured_image_uri", imageUri.toString())
                startActivity(intent)
                progressDialog.dismiss()
                binding.captureButton.isEnabled = true
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

    override fun onStop() {
        super.onStop()
        faceContourProcessor.stop()
    }
}
