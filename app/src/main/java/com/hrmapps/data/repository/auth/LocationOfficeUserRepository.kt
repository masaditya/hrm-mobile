package com.hrmapps.data.repository.auth

import com.hrmapps.data.api.ApiService
import com.hrmapps.data.model.response.LocationOfficeUserResponse
import retrofit2.Call

class LocationOfficeUserRepository(private val apiService: ApiService) {
    fun getLocationOfficeUser(token: String): Call<LocationOfficeUserResponse> {
        return apiService.getLocationOfficeUser("Bearer $token")
    }
}