package com.hrmapps.data.repository

import com.hrmapps.data.api.ApiService
import com.hrmapps.data.model.response.CheckInResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class CheckInRepository(private val apiService: ApiService) {

    suspend fun checkIn(
        companyId: String,
        userId: String,
        clockInTime: String,
        autoClockOut: String,
        clockInIp: String,
        late: String,
        latitude: String,
        longitude: String,
        workFromType: String,
        overwriteAttendance: String,
        photo: File,
        token: String
    ): Result<CheckInResponse> {
        return try {
            val requestBody = createRequestBody(
                companyId, userId, clockInTime, autoClockOut, clockInIp, late,
                latitude, longitude, workFromType, overwriteAttendance
            )

            val photoPart = createPhotoPart(photo)

            val response = apiService.checkIn("Bearer $token", requestBody, photoPart)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun createRequestBody(
        companyId: String,
        userId: String,
        clockInTime: String,
        autoClockOut: String,
        clockInIp: String,
        late: String,
        latitude: String,
        longitude: String,
        workFromType: String,
        overwriteAttendance: String
    ): Map<String, RequestBody> {
        val requestBodyMap = mutableMapOf<String, RequestBody>()

        requestBodyMap["company_id"] = companyId.toRequestBody("text/plain".toMediaType())
        requestBodyMap["user_id"] = userId.toRequestBody("text/plain".toMediaType())
        requestBodyMap["clock_in_time"] = clockInTime.toRequestBody("text/plain".toMediaType())
        requestBodyMap["auto_clock_out"] = autoClockOut.toRequestBody("text/plain".toMediaType())
        requestBodyMap["clock_in_ip"] = clockInIp.toRequestBody("text/plain".toMediaType())
        requestBodyMap["late"] = late.toRequestBody("text/plain".toMediaType())
        requestBodyMap["latitude"] = latitude.toRequestBody("text/plain".toMediaType())
        requestBodyMap["longitude"] = longitude.toRequestBody("text/plain".toMediaType())
        requestBodyMap["work_from_type"] = workFromType.toRequestBody("text/plain".toMediaType())
        requestBodyMap["overwrite_attendance"] = overwriteAttendance.toRequestBody("text/plain".toMediaType())

        return requestBodyMap
    }

    private fun createPhotoPart(photo: File): MultipartBody.Part {
        val requestFile = photo.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("photo", photo.name, requestFile)
    }
}
