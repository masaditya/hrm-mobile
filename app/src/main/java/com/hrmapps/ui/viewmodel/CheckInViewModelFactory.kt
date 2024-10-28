package com.hrmapps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hrmapps.data.repository.CheckInRepository

class CheckInViewModelFactory(private val repository: CheckInRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CheckInViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CheckInViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
