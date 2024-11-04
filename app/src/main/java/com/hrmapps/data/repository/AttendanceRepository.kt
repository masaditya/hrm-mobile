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
        locationId: Int,
        userId: Int
    ): Flow<PagingData<AttendanceData>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                AttendancePagingSource(apiService, token, workingFrom, locationId, userId)
            }
        ).flow
    }
}
