package com.hrmapps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.hrmapps.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    fun login(email: String, password: String) = liveData(Dispatchers.IO) {
        try {
            val response = repository.login(email, password)
            emit(response)
        } catch (e: Exception) {
            emit(null)
        }
    }
}
