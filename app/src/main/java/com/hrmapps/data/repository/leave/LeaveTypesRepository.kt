package com.hrmapps.data.repository.leave

import com.hrmapps.data.api.ApiService
import com.hrmapps.data.model.leave.LeaveTypes
import com.hrmapps.data.model.response.LeaveTypesResponse
import retrofit2.Call

class LeaveTypesRepository(private val apiService: ApiService)  {
    fun getLeaveTypes(token: String): Call<LeaveTypesResponse> {
        return apiService.getLeaveTypes("Bearer $token")
    }
}