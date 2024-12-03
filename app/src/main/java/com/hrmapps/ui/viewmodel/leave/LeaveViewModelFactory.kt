package com.hrmapps.ui.viewmodel.leave

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hrmapps.data.repository.leave.ListLeaveRepository

@Suppress("UNCHECKED_CAST")
class LeaveViewModelFactory(private val repository: ListLeaveRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LeaveViewModel::class.java)) {
            return LeaveViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }
}