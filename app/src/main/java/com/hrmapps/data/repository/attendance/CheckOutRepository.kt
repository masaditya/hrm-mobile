package com.hrmapps.data.repository.attendance

import com.hrmapps.data.api.ApiService
import com.hrmapps.data.model.response.CheckOutResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckOutRepository(private val apiService: ApiService) {

    fun checkOut(
        token: String,
        id: Int,
        userId: Int,
        clockOutTime: String,
        autoClockOut: String,
        clockOutIp: String,
        halfDay: String,
        onResult: (Result<CheckOutResponse>) -> Unit
    ) {

        val call = apiService.checkOut("Bearer $token", id, userId, clockOutTime, autoClockOut, clockOutIp, halfDay)


        call.enqueue(object : Callback<CheckOutResponse> {
            override fun onResponse(
                call: Call<CheckOutResponse>,
                response: Response<CheckOutResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        onResult(Result.success(it))
                    } ?: run {
                        onResult(Result.failure(Exception("Empty response body")))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    onResult(Result.failure(Exception("$errorBody")))
                }
            }

            override fun onFailure(call: Call<CheckOutResponse>, t: Throwable) {
                onResult(Result.failure(t))
            }
        })
    }
}
