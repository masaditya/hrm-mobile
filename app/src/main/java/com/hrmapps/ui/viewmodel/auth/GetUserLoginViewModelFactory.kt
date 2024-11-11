package com.hrmapps.ui.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hrmapps.data.repository.auth.GetUserRepository

class GetUserLoginViewModelFactory(private val repository: GetUserRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GetUserLoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GetUserLoginViewModel(repository) as T
        }
       throw IllegalArgumentException("Unknown ViewModel class")

    }
}