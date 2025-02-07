package com.hrmpandjiadhi.ui.viewmodel.leave

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hrmpandjiadhi.data.repository.leave.LeaveTypesRepository

@Suppress("UNCHECKED_CAST")
class LeaveTypesViewModelFactory(private val repository: LeaveTypesRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LeaveTypesViewModel::class.java)) {
            return LeaveTypesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }
}