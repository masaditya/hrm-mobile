package com.hrmapps.data.repository.notice

import com.google.gson.Gson
import com.hrmapps.data.api.ApiService
import com.hrmapps.data.model.notice.Notice
import com.hrmapps.data.model.response.NoticeDetailResponse
import okhttp3.ResponseBody

class NoticeRepository(private val apiService: ApiService) {

    data class ErrorResponse(
        val success: Boolean,
        val message: String? = null
    )

    private fun parseErrorBody(errorBody: ResponseBody?): String {
        return try {
            val gson = Gson()
            val errorResponse = gson.fromJson(errorBody?.string(), ErrorResponse::class.java)
            errorResponse.message ?: "Unknown error occurred"
        } catch (e: Exception) {
            e.printStackTrace()
            "Error parsing error response"
        }
    }

    suspend fun fetchNotices(token: String, userId: Int): Result<List<Notice>> {
        return try {
            val response = apiService.getNotices("Bearer $token", userId)
            if (response.isSuccessful) {
                Result.success(response.body()?.data ?: emptyList())
            } else {
                val errorMessage = parseErrorBody(response.errorBody())
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
    suspend fun markNoticeAsRead(token: String, noticeId: Int): Result<Unit> {
        return try {
            val response = apiService.markNoticeAsRead("Bearer $token",noticeId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMessage = parseErrorBody(response.errorBody())
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
    suspend fun fetchDetailNotice(token: String, noticeId: Int): Result<NoticeDetailResponse> {
        return try {
            val response = apiService.getNoticeDetail("Bearer $token", noticeId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: throw Exception("Response body is null"))
            } else {
                val errorMessage = parseErrorBody(response.errorBody())
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
