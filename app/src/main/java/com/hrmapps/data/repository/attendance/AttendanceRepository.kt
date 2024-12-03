package com.hrmapps.data.repository.attendance

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import com.hrmapps.data.api.ApiService
import com.hrmapps.data.model.attendance.AttendanceData
import com.hrmapps.data.paging.AttendancePagingSource

class AttendanceRepository(private val apiService: ApiService) {

    suspend fun fetchAttendance(
        token: String,
        workingFrom: String,
        userId: Int,
        page: Int,
        limit: Int
    ): List<AttendanceData> {
        val response = apiService.getAttendance("Bearer $token", page, limit, workingFrom, userId)
        return response.data ?: emptyList()
    }
}
