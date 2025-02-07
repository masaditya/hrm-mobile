package com.hrmpandjiadhi.ui.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.hrmpandjiadhi.R
import com.hrmpandjiadhi.data.api.RetrofitBuilder
import com.hrmpandjiadhi.data.repository.notice.NoticeRepository
import com.hrmpandjiadhi.databinding.ActivityDetailNoticeBinding
import com.hrmpandjiadhi.ui.viewmodel.notice.NoticeViewModel
import com.hrmpandjiadhi.ui.viewmodel.notice.NoticeViewModelFactory

class DetailNoticeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailNoticeBinding
    private lateinit var viewModel: NoticeViewModel
    private val noticeId by lazy { intent.getIntExtra("noticeId", -1) }
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.mToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        sharedPreferences = getSharedPreferences("userPref", MODE_PRIVATE)
        val apiService = RetrofitBuilder.apiService
        val repository = NoticeRepository(apiService)
        val factory = NoticeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(NoticeViewModel::class.java)

        val token = sharedPreferences.getString("token", "")
        if (token != null) {
            fetchDetailNotice(token, noticeId)
        }

    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun fetchDetailNotice(token: String, noticeId: Int) {
        viewModel.fetchDetailNotice(token, noticeId)
        var urlDownload = ""
        viewModel.noticeDetail.observe(this) { noticeDetail ->
            val dataNotice = noticeDetail.data
            if (dataNotice == null) {
                onBackPressedDispatcher.onBackPressed()
            }else{
                binding.tvHeading.text = dataNotice.heading
                binding.tvDate.text = "${dataNotice.time}, ${dataNotice.date}"
                binding.tvFrom.text = "To : ${dataNotice.to}"
                if(dataNotice.filename != null){
                    binding.llDownloadFile.visibility = View.VISIBLE
                    binding.tvFileName.text = dataNotice.filename
                }else{
                    binding.llDownloadFile.visibility = View.GONE
                }
                urlDownload = dataNotice.hashname
                binding.webView.settings.apply {
                    javaScriptEnabled = true
                }
                binding.webView.webViewClient = WebViewClient()

                binding.webView.loadData(dataNotice.description, "text/html", "UTF-8")
            }


        }
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.loadingBar.visibility = View.VISIBLE
            } else {
                binding.loadingBar.visibility = View.GONE
            }
        }
        viewModel.errorMessage.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(this, errorMessage.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        binding.ivDownload.setOnClickListener {
            openBrowser(urlDownload)
        }

    }

    private fun openBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
}