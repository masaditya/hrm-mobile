package com.hrmapps.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import com.hrmapps.data.api.ApiService
import com.hrmapps.data.model.AttendanceData
import com.hrmapps.data.paging.AttendancePagingSource

class AttendanceRepository(private val apiService: ApiService) {

    fun getPagedAttendance(
        token: String,
        workingFrom: String,
        userId: Int
    ): Flow<PagingData<AttendanceData>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                AttendancePagingSource(apiService, token, workingFrom, userId)
            }
        ).flow
    }
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
