package com.hrmapps.data.repository.leave

import com.hrmapps.data.api.ApiService
import com.hrmapps.data.model.leave.Leave

class ListLeaveRepository(private val apiService: ApiService) {

    suspend fun fetchLeave(
        token: String,
        page: Int,
        limit: Int,
        userId: Int
    ): Result<List<Leave>> {
        return try {
            val response = apiService.getLeave("Bearer $token", page, limit, userId)

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.data != null) {
                    Result.success(body.data)
                } else {
                    Result.success(emptyList())
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = "$errorBody"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            println("Error fetching leave data: ${e.message}")
            Result.failure(e)
        }
    }
}
