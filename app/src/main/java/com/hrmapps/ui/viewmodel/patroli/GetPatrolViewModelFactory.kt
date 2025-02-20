package com.hrmapps.ui.viewmodel.patroli

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hrmapps.data.repository.patroli.GetPatrolTypesRepository

class GetPatrolViewModelFactory(private val getPatrolTypesRepository: GetPatrolTypesRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GetPatrolViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GetPatrolViewModel(getPatrolTypesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}