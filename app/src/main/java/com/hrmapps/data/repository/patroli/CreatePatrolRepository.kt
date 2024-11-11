package com.hrmapps.data.repository.patroli

import com.hrmapps.data.api.ApiService
import com.hrmapps.data.model.response.CreatePatrolResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class CreatePatrolRepository(private val apiService: ApiService) {

    suspend fun createPatrol(
        token: String,
        name: String,
        patrolTypeId: String,
        description: String,
        latitude: String,
        longitude: String,
        added_by: String,
        image: File
    ): Result<CreatePatrolResponse> {
            return try {
                val requestBody = createRequestBody(name, patrolTypeId, description, latitude, longitude, added_by)
                val photoPart = createPhotoPart(image)

                val response = apiService.createPatrol("Bearer $token", requestBody, photoPart)
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
    }
    private fun createRequestBody(
        name: String,
        patrolTypeId: String,
        description: String,
        latitude: String,
        longitude: String,
        addedBy: String
    ): Map<String, RequestBody> {
        val requestBodyMap = mutableMapOf<String, RequestBody>()
        requestBodyMap["name"] = name.toRequestBody("text/plain".toMediaTypeOrNull())
        requestBodyMap["patrol_type_id"] = patrolTypeId.toRequestBody("text/plain".toMediaTypeOrNull())
        requestBodyMap["description"] = description.toRequestBody("text/plain".toMediaTypeOrNull())
        requestBodyMap["latitude"] = latitude.toRequestBody("text/plain".toMediaTypeOrNull())
        requestBodyMap["longitude"] = longitude.toRequestBody("text/plain".toMediaTypeOrNull())
        requestBodyMap["added_by"] = addedBy.toRequestBody("text/plain".toMediaTypeOrNull())


        return requestBodyMap
    }

    private fun createPhotoPart(photo: File): MultipartBody.Part {
        val requestFile = photo.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("image", photo.name, requestFile)
    }
}
