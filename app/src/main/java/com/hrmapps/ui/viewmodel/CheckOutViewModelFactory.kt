package com.hrmapps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hrmapps.data.repository.CheckInStatusRepository
import com.hrmapps.data.repository.CheckOutRepository

class CheckOutViewModelFactory(private val repository: CheckOutRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CheckOutViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CheckOutViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}