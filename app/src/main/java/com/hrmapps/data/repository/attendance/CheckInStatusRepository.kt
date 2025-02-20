package com.hrmapps.data.repository.attendance

import com.hrmapps.data.api.ApiService
import com.hrmapps.data.model.response.CheckInStatusResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckInStatusRepository(private val apiService: ApiService) {

    fun getCheckInStatus(
        token: String,
        userId: Int,
        onResult: (Result<CheckInStatusResponse>) -> Unit
    ) {
        val call = apiService.getCheckInStatus("Bearer $token", userId)

        call.enqueue(object : Callback<CheckInStatusResponse> {
            override fun onResponse(
                call: Call<CheckInStatusResponse>,
                response: Response<CheckInStatusResponse>
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

            override fun onFailure(call: Call<CheckInStatusResponse>, t: Throwable) {
                onResult(Result.failure(t))
            }
        })
    }
}
