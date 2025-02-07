package com.hrmpandjiadhi.ui.viewmodel.patroli

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hrmpandjiadhi.data.repository.patroli.CreatePatrolRepository

class CreatePatrolViewModelFactory(private val repository: CreatePatrolRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreatePatrolViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreatePatrolViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}