package com.hrmapps.ui.view.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.hrmapps.R
import com.hrmapps.data.model.Leave
import com.hrmapps.databinding.ActivityLeaveBinding
import com.hrmapps.ui.view.LeaveAdapter

class LeaveActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaveBinding
    private lateinit var leaveAdapter: LeaveAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLeaveBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val leaveList = listOf(
            Leave("01 Jan 2024", "05 Jan 2024", "Sakit", "Alasan cuti karena sakit demam."),
            Leave("12 Feb 2024", "16 Feb 2024", "Libur", "Liburan keluarga.")
        )

        leaveAdapter = LeaveAdapter(leaveList)
        binding.rvLeave.apply {
            adapter = leaveAdapter
            layoutManager = LinearLayoutManager(this@LeaveActivity)
        }
    }
}