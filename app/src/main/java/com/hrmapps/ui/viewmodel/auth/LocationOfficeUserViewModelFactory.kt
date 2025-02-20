package com.hrmapps.ui.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hrmapps.data.repository.auth.LocationOfficeUserRepository

class LocationOfficeUserViewModelFactory(private val repository: LocationOfficeUserRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocationOfficeUserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LocationOfficeUserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}