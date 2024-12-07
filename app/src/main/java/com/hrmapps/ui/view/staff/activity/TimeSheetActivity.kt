package com.hrmapps.ui.view.staff.activity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.hrmapps.R
import com.hrmapps.databinding.ActivityTimeSheetBinding
import com.hrmapps.ui.viewmodel.auth.AuthViewModel
import com.hrmapps.ui.viewmodel.auth.AuthViewModelFactory
import com.hrmapps.data.api.ApiService
import com.hrmapps.data.api.RetrofitBuilder
import com.hrmapps.data.repository.auth.AuthRepository
import com.hrmapps.ui.view.activity.LoginActivity

class TimeSheetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTimeSheetBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private var fileChooserCallback: ValueCallback<Array<Uri>>? = null

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            fileChooserCallback?.onReceiveValue(arrayOf(uri))
        } else {
            fileChooserCallback?.onReceiveValue(null)
        }
        fileChooserCallback = null
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimeSheetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("isLoggedIn", MODE_PRIVATE)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupViewModel()
        setupWebView()
        setupOnBackPressed()
        observeViewModel()
    }


    private fun setupViewModel() {
        val apiService: ApiService = RetrofitBuilder.apiService
        val repository = AuthRepository(apiService)
        val factory = AuthViewModelFactory(repository)
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val webSettings: WebSettings = binding.webView.settings
        webSettings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            useWideViewPort = true
            allowFileAccess = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
            loadWithOverviewMode = true
        }

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                binding.loadingBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                binding.loadingBar.visibility = View.GONE
            }
        }
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                fileChooserCallback = filePathCallback
                galleryLauncher.launch("image/*") // Open gallery for image selection
                return true
            }

        }
        binding.webView.setDownloadListener { url, userAgent, contentDisposition, mimeType, _ ->
            val request = DownloadManager.Request(Uri.parse(url)).apply {
                setMimeType(mimeType)
                addRequestHeader("User-Agent", userAgent)
                setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType))
                setDescription("Downloading file...")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType))
            }

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)

            Toast.makeText(this, "Download started...", Toast.LENGTH_SHORT).show()
        }


        binding.webView.loadUrl("https://i.mahawangsa.com/fops")
    }

    private fun setupOnBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.webView.canGoBack()) {
                    binding.webView.goBack()
                } else {
                    finish()
                }
            }
        })
    }

    private fun observeViewModel() {
        authViewModel.logoutResult.observe(this) { result ->
            when {
                result.isSuccess -> {
                    sharedPreferences.edit().clear().apply()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                result.isFailure -> {
                    Toast.makeText(this, "Logout failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        authViewModel.isLoading.observe(this) { isLoading ->
            binding.loadingBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        authViewModel.errorMessage.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }


}
