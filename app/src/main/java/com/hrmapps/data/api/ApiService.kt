package com.hrmapps.data.api

import androidx.lifecycle.LiveData
import com.hrmapps.data.model.response.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

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

}