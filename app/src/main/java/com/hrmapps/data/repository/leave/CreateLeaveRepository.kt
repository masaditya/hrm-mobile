package com.hrmapps.data.repository.leave

import com.hrmapps.data.api.ApiService
import com.hrmapps.data.model.response.CreateLeaveResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
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

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
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
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("image", file.name, requestFile)

    }

}


