package com.hrmapps.data.repository.leave

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.hrmapps.data.api.ApiService
import com.hrmapps.data.model.attendance.AttendanceData
import com.hrmapps.data.model.leave.Leave
import com.hrmapps.data.paging.LeavePagingSource

class ListLeaveRepository(private val apiService: ApiService) {

    fun getPagedLeaves(token: String, userId: Int) = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            LeavePagingSource(apiService, token, userId)
        }
    ).liveData

    suspend fun fetchLeave(
        token: String,
        page: Int,
        limit: Int,
        userId: Int

    ): List<Leave> {
        val response = apiService.getLeave("Bearer $token", page, limit, userId)
        return response.data ?: emptyList()
    }
}
