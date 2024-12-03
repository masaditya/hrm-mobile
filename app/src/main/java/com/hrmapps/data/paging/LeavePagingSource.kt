package com.hrmapps.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hrmapps.data.api.ApiService
import com.hrmapps.data.model.leave.Leave

class LeavePagingSource(
    private val apiService: ApiService,
    private val token: String,
    private val userId: Int
) : PagingSource<Int, Leave>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Leave> {
        val page = params.key ?: 1
        return try {
            val response = apiService.getLeave("Bearer $token", page, 20, userId)
            val leaveList = response.data
            LoadResult.Page(
                data = leaveList,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.current_page < response.last_page) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Leave>): Int? {
        return state.anchorPosition?.let { state.closestPageToPosition(it)?.prevKey }
    }
}
