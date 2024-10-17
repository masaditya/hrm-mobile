package com.hrmapps.ui.view.fragment

import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hrmapps.databinding.FragmentHomeBinding
import com.hrmapps.ui.view.activity.PresentActivity
import java.text.SimpleDateFormat
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val handler = Handler()
    private val runnable: Runnable = object : Runnable {
        override fun run() {
            updateTimeAndDate()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        handler.post(runnable)
    }

    private fun setupUI() {
        binding.linearLayout2.apply {
        }
        binding.btCheckpoint.setOnClickListener {

        }
        binding.btPresent.setOnClickListener {
            startActivity(Intent(requireContext(), PresentActivity::class.java))
        }
    }

    private fun updateTimeAndDate() {
        val calendar = Calendar.getInstance()
        val hours = calendar.get(Calendar.HOUR)
        val minutes = calendar.get(Calendar.MINUTE)
        val seconds = calendar.get(Calendar.SECOND)
        val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"

        val dateFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
        val date = dateFormat.format(calendar.time)

        binding.tvFormatTime.text = amPm
        binding.tvHours.text = String.format("%02d", hours)
        binding.tvMinutes.text = String.format("%02d", minutes)
        binding.tvSecond.text = String.format("%02d", seconds)
        binding.tvDate.text = date
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(runnable)
        _binding = null
    }
}