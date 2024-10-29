package com.hrmapps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hrmapps.data.repository.CheckInStatusRepository

class CheckInStatusViewModelFactory(
    private val repository: CheckInStatusRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CheckInStatusViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CheckInStatusViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
