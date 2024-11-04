package com.hrmapps.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hrmapps.data.api.ApiService
import com.hrmapps.data.model.AttendanceData
import retrofit2.HttpException
import java.io.IOException

class AttendancePagingSource(
    private val apiService: ApiService,
    private val token: String,
    private val workingFrom: String,
    private val userId: Int
) : PagingSource<Int, AttendanceData>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AttendanceData> {
        val page = params.key ?: 1
        return try {
            val response = apiService.getAttendance("Bearer $token", page, params.loadSize, workingFrom, userId)
            LoadResult.Page(
                data = response.data ?: emptyList(),
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.data.isNullOrEmpty()) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, AttendanceData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
