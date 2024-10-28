package com.hrmapps.data.api

import androidx.lifecycle.LiveData
import com.hrmapps.data.model.response.CheckInResponse
import com.hrmapps.data.model.response.LoginResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("api/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("android_id") androidId: String
    ): Call<LoginResponse>

    @POST("api/logout")
    fun logout(
        @Header("Authorization") token: String
    ): Call<Void>

    @Multipart
    @POST("api/attendance/checkin")
    suspend fun checkIn(
        @Header("Authorization") token: String,
        @PartMap requestBody: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part photo: MultipartBody.Part
    ): CheckInResponse

    fun getCheckInStatus(
        @Header("Authorization") token: String,
        @Query("user_id") userId: Int
    ): Call<ResponseBody>
}