package com.hrmapps.data.repository.attendance

import com.hrmapps.data.api.ApiService
import com.hrmapps.data.model.attendance.AttendanceData
import retrofit2.HttpException

class AttendanceRepository(private val apiService: ApiService) {

    suspend fun fetchAttendance(
        token: String,
        workingFrom: String,
        userId: Int,
        page: Int,
        limit: Int
    ): Result<List<AttendanceData>> {
        return try {
            val response = apiService.getAttendance("Bearer $token", page, limit, workingFrom, userId)
            Result.success(response.data ?: emptyList())
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Result.failure(Exception("$errorBody"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
