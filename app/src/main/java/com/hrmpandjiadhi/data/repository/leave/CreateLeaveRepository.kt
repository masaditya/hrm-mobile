package com.hrmpandjiadhi.data.repository.leave

import com.hrmpandjiadhi.data.api.ApiService
import com.hrmpandjiadhi.data.model.response.CreateLeaveResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class CreateLeaveRepository(private val apiService: ApiService) {

    suspend fun createLeave(
        token: String,
        companyId: String,
        userId: String,
        leaveTypeId: String,
        startDate: String,
        endDate: String,
        reason: String,
        addedBy: String,
        file: File? // Mengubah parameter file menjadi nullable
    ): Result<CreateLeaveResponse> {
        return try {
            val requestBody = createRequestBody(companyId, userId, leaveTypeId, startDate, endDate, reason, addedBy)
            val photoPart = file?.let { createPhotoPart(it) }
            val response = if (photoPart != null) {
                apiService.createLeave("Bearer $token", requestBody, photoPart)
            } else {
                apiService.createLeave("Bearer $token", requestBody)
            }

            if (response.success) {
                Result.success(response)
            } else {
                Result.failure(Exception(response.message ))
            }
        } catch (e: HttpException) {
            // Tangani error HTTP (misalnya 404, 500)
            val errorBody = e.response()?.errorBody()?.string()
            Result.failure(Exception("$errorBody"))
        } catch (e: Exception) {
            // Tangani kesalahan lain yang mungkin terjadi
            Result.failure(Exception("Unexpected error: ${e.message}"))
        }
    }

    private fun createRequestBody(
        companyId: String,
        userId: String,
        leaveTypeId: String,
        startDate: String,
        endDate: String,
        reason: String,
        addedBy: String
    ): Map<String, RequestBody> {
        val requestBodyMap = mutableMapOf<String, RequestBody>()
        requestBodyMap["company_id"] = companyId.toRequestBody("text/plain".toMediaTypeOrNull())
        requestBodyMap["user_id"] = userId.toRequestBody("text/plain".toMediaTypeOrNull())
        requestBodyMap["leave_type_id"] =
            leaveTypeId.toRequestBody("text/plain".toMediaTypeOrNull())
        requestBodyMap["leave_date"] = startDate.toRequestBody("text/plain".toMediaTypeOrNull())
        requestBodyMap["leave_end_date"] = endDate.toRequestBody("text/plain".toMediaTypeOrNull())
        requestBodyMap["reason"] = reason.toRequestBody("text/plain".toMediaTypeOrNull())
        requestBodyMap["added_by"] = addedBy.toRequestBody("text/plain".toMediaTypeOrNull())

        return requestBodyMap
    }

    private fun createPhotoPart(file: File): MultipartBody.Part {
        val mimeType = getMimeType(file) ?: "application/octet-stream"
        val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("filename", file.name, requestFile)
    }

    private fun getMimeType(file: File): String? {
        return when (file.extension.lowercase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "pdf" -> "application/pdf"
            else -> null
        }
    }


}


