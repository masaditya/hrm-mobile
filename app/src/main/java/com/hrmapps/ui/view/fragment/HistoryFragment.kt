package com.hrmapps.ui.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.animation.AnimationUtils
import com.hrmapps.R
import com.hrmapps.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        setupSpinner()
        return binding.root
    }


    private fun setupSpinner() {
        val months = listOf("January", "February", "March", "April", "May", "June")

        binding.powerSpinnerView.apply {
            setItems(months)
            spinnerPopupBackground = resources.getDrawable(R.drawable.spinner_background)
            setOnSpinnerItemSelectedListener<String> { _, selectedItem, _, _ ->

            }

        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
