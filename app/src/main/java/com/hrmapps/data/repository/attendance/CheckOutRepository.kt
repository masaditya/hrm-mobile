package com.hrmapps.data.repository.attendance

import com.hrmapps.data.api.ApiService
import com.hrmapps.data.model.response.CheckOutResponse
import retrofit2.Call

class CheckOutRepository(private val apiService: ApiService) {

    fun checkOut(
        token: String,
        id: Int,
        userId: Int,
        clockOutTime: String,
        autoClockOut: String,
        clockOutIp: String,
        halfDay: String
    ): Call<CheckOutResponse> {
        return apiService.checkOut("Bearer $token",id, userId, clockOutTime, autoClockOut, clockOutIp, halfDay)
    }
}