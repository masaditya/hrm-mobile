package com.hrmapps.data.repository.auth

import com.hrmapps.data.api.ApiService
import com.hrmapps.data.model.response.GetUserResponse
import retrofit2.Call

class GetUserRepository(private val apiService: ApiService) {
    fun getUser(token: String): Call<GetUserResponse> {
        return apiService.getUserLogin("Bearer $token")
    }

}