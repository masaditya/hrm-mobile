package com.hrmapps.data.api

import com.hrmapps.data.model.request.LoginRequest
import com.hrmapps.data.model.response.LoginResponse
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
}