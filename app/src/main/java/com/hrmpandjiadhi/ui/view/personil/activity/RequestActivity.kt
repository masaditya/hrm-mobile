package com.hrmpandjiadhi.ui.view.personil.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hrmpandjiadhi.R
import com.hrmpandjiadhi.data.model.request.Job
import com.hrmpandjiadhi.databinding.ActivityRequestBinding

class RequestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRequestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setSupportActionBar(binding.mToolbar)
        binding.mToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val jobList = listOf(
            Job("Posisi 1", "2024-10-20", "2024-10-22", "John Doe"),
            Job("Posisi 2", "2024-10-23", "2024-10-25", "Jane Smith"),
            // Tambahkan lebih banyak data pekerjaan jika perlu
        )

//        val adapter = JobAdapter(jobList) { job ->
//            navigateToJobDetail(job)
//        }
//
//        binding.recyclerViewJobs.layoutManager = LinearLayoutManager(this)
//        binding.recyclerViewJobs.adapter = adapter
//
//        binding.buttonSubmit.setOnClickListener {
//            val workDetails = binding.editTextWorkDetails.text.toString()
//            // Kirim permohonan lembur
//        }
    }

//    private fun navigateToJobDetail(job: Job) {
//        val intent = Intent(this, JobDetailActivity::class.java)
//        intent.putExtra("position", job.position)
//        intent.putExtra("startDate", job.startDate)
//        intent.putExtra("endDate", job.endDate)
//        intent.putExtra("supervisor", job.supervisor)
//        startActivity(intent)
//    }
}