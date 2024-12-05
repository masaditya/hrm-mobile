package com.hrmapps.ui.view.personil.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hrmapps.R
import com.hrmapps.databinding.ActivityJobDetailBinding
import com.hrmapps.databinding.ItemJobBinding

class JobDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJobDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityJobDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.mToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val position = intent.getStringExtra("position")
        val startDate = intent.getStringExtra("startDate")
        val endDate = intent.getStringExtra("endDate")
        val supervisor = intent.getStringExtra("supervisor")

        binding.textViewJobPosition.text = position
        binding.textViewDateRange.text = "$startDate - $endDate"
        binding.textViewSupervisor.text = supervisor
    }
}