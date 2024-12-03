package com.hrmapps.ui.viewmodel.leave

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hrmapps.data.repository.leave.CreateLeaveRepository

@Suppress("UNCHECKED_CAST")
class CreateLeaveViewModelFactory(private val repository: CreateLeaveRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateLeaveViewModel::class.java)) {
            return CreateLeaveViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }
}