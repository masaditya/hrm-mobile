package com.hrmpandjiadhi.data.repository.leave

import com.hrmpandjiadhi.data.api.ApiService
import com.hrmpandjiadhi.data.model.response.LeaveTypesResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LeaveTypesRepository(private val apiService: ApiService) {

    fun getLeaveTypes(token: String, onResult: (Result<LeaveTypesResponse>) -> Unit) {
        val call = apiService.getLeaveTypes("Bearer $token")

        call.enqueue(object : Callback<LeaveTypesResponse> {
            override fun onResponse(
                call: Call<LeaveTypesResponse>,
                response: Response<LeaveTypesResponse>
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

            override fun onFailure(call: Call<LeaveTypesResponse>, t: Throwable) {
                onResult(Result.failure(t))
            }
        })
    }
}
