package com.hrmapps.ui.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.captureButton.isEnabled = false
        binding.captureButton.setOnClickListener {
            captureImage()
        }

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
        val rectangles = mutableListOf<RectF>()
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
                val intent = Intent(this, PresentCheckInActivity::class.java)
                intent.putExtra("captured_image_uri", imageUri.toString())
                startActivity(intent)
                finish()
            } else {
                // Handle error saving the image
            }
        }
    }


    override fun onStop() {
        super.onStop()
        faceContourProcessor.stop()
    }
}
