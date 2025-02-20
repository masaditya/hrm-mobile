package com.hrmapps.data.api

import com.hrmapps.data.model.response.AttendanceResponse
import com.hrmapps.data.model.response.CheckInResponse
import com.hrmapps.data.model.response.CheckInStatusResponse
import com.hrmapps.data.model.response.CheckOutResponse
import com.hrmapps.data.model.response.CheckPointResponse
import com.hrmapps.data.model.response.CreatePatrolResponse
import com.hrmapps.data.model.response.GetUserResponse
import com.hrmapps.data.model.response.CreateLeaveResponse
import com.hrmapps.data.model.response.LeaveListResponse
import com.hrmapps.data.model.response.LeaveTypesResponse
import com.hrmapps.data.model.response.LocationOfficeUserResponse
import com.hrmapps.data.model.response.LoginResponse
import com.hrmapps.data.model.response.NoticeDetailResponse
import com.hrmapps.data.model.response.NoticeResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
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

    @FormUrlEncoded
    @PATCH("api/attendance/checkout")
     fun checkOut(
        @Header("Authorization") token: String,
        @Field("id") id: Int,
        @Field("user_id") userId: Int,
        @Field("clock_out_time") clockOutTime: String,
        @Field("auto_clock_out") autoClockOut: String,
        @Field("clock_out_ip") clockOutIp: String,
        @Field("half_day") halfDay: String
    ): Call<CheckOutResponse>

    @GET("api/user-logged")
    fun getUserLogin(
        @Header("Authorization") token: String,
    ): Call<GetUserResponse>

    @PUT("api/user/{id}/email")
    fun updateEmail(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body requestBody: RequestBody
    ): Call<ResponseBody>

    @GET("api/attendance/check")
    fun getCheckInStatus(
        @Header("Authorization") token: String,
        @Query("user_id") userId: Int
    ): Call<CheckInStatusResponse>

    @GET("api/attendance")
    suspend fun getAttendance(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("working_from") workingFrom: String,
        @Query("user_id") userId: Int
    ): AttendanceResponse

    @GET("api/attendance/get-user-detail")
    fun getLocationOfficeUser(
        @Header("Authorization") token: String,
    ): Call<LocationOfficeUserResponse>

    @Multipart
    @POST("api/patrol/create")
    suspend fun createPatrol(
        @Header("Authorization") token: String,
        @PartMap requestBody: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part image: MultipartBody.Part
    ): CreatePatrolResponse

    @GET("api/patrol-types")
    fun getPatrolTypes(
        @Header("Authorization") token: String,
    ): Call<CheckPointResponse>

    @FormUrlEncoded
    @PATCH("api/update-password")
    fun changePassword(
        @Header("Authorization") token: String,
        @Field("password") password: String,
        @Field("password_confirmation") passwordConfirmation: String
    ): Call<ResponseBody>

    @GET("api/leave-types")
    fun getLeaveTypes(
        @Header("Authorization") token: String,
    ): Call<LeaveTypesResponse>

    @Multipart
    @POST("api/leave/create")
    suspend fun createLeave(
        @Header("Authorization") token: String,
        @PartMap requestBody: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part image: MultipartBody.Part? = null
    ): CreateLeaveResponse

    @GET("api/leaves")
    suspend fun getLeave(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("user_id") userId: Int
    ): Response<LeaveListResponse>

    @GET("api/notices")
    suspend fun getNotices(
        @Header("Authorization") token: String,
        @Query("user_id") userId: Int
    ): Response<NoticeResponse>

    @PUT("api/notice-views/read")
    suspend fun markNoticeAsRead(
        @Header("Authorization") token: String,
        @Query("notice_id") noticeId: Int
    ): Response<Unit>

    @GET("api/notice/detail")
    suspend fun getNoticeDetail(
        @Header("Authorization") token: String,
        @Query("notice_id") noticeId: Int
    ): Response<NoticeDetailResponse>
}