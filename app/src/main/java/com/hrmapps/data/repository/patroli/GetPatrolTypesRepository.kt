package com.hrmapps.data.repository.patroli

import com.hrmapps.data.api.ApiService
import com.hrmapps.data.model.response.CheckPointResponse
import retrofit2.Call

class GetPatrolTypesRepository(private val apiService: ApiService)  {
    fun getPatrolTypes(token: String): Call<CheckPointResponse> {
        return apiService.getPatrolTypes("Bearer $token")
    }
}