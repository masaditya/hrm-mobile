 package com.hrmapps.ui.view.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hrmapps.R
import com.hrmapps.databinding.ActivityAddNewFormLeaveBinding

 class AddNewFormLeaveActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNewFormLeaveBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddNewFormLeaveBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.mToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.editTextStartDate.setOnClickListener {
            Toast.makeText(this, "Pilih Tanggal Mulai Cuti", Toast.LENGTH_SHORT).show()
        }

        binding.editTextEndDate.setOnClickListener {
            Toast.makeText(this, "Pilih Tanggal Selesai Cuti", Toast.LENGTH_SHORT).show()
        }

        binding.buttonSubmit.setOnClickListener {
            val leaveType = binding.editTextLeaveType.text.toString()
            val startDate = binding.editTextStartDate.text.toString()
            val endDate = binding.editTextEndDate.text.toString()
            val leaveReason = binding.editTextLeaveReason.text.toString()

            if (leaveType.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || leaveReason.isEmpty()) {
                Toast.makeText(this, "Harap lengkapi semua data", Toast.LENGTH_SHORT).show()
            } else {
                finish()
                Toast.makeText(this, "Permohonan cuti terkirim", Toast.LENGTH_SHORT).show()
            }
        }
    }
}