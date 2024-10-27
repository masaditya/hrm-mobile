package com.hrmapps.data.repository

import com.hrmapps.data.api.ApiService
import com.hrmapps.data.model.request.LoginRequest
import com.hrmapps.data.model.response.LoginResponse

class AuthRepository(private val apiService: ApiService) {

    suspend fun login(email: String, password: String): LoginResponse {
        val loginRequest = LoginRequest(email, password)
        return apiService.login(loginRequest)
    }
}
