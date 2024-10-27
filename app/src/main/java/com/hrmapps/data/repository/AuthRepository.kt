package com.hrmapps.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hrmapps.data.api.ApiService
import com.hrmapps.data.model.request.LoginRequest
import com.hrmapps.data.model.response.LoginResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call

class AuthRepository(private val apiService: ApiService) {
    fun login(email: String, password: String): Call<LoginResponse> {
        return apiService.login(email, password)
    }
}