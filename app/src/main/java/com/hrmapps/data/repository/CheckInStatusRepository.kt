package com.hrmapps.data.repository

import com.hrmapps.data.api.ApiService
import com.hrmapps.data.model.response.CheckInStatusResponse
import retrofit2.Call

class CheckInStatusRepository(private val apiService: ApiService) {

    fun getCheckInStatus(token: String, userId: Int): Call<CheckInStatusResponse> {
        return apiService.getCheckInStatus("Bearer $token", userId)
    }
}
