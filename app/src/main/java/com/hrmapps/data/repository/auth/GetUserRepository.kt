package com.hrmapps.data.repository.auth

import com.hrmapps.data.api.ApiService
import com.hrmapps.data.model.response.GetUserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetUserRepository(private val apiService: ApiService) {

    fun getUser(token: String, onResult: (Result<GetUserResponse>) -> Unit) {

        val call = apiService.getUserLogin("Bearer $token")

        call.enqueue(object : Callback<GetUserResponse> {
            override fun onResponse(
                call: Call<GetUserResponse>,
                response: Response<GetUserResponse>
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

            override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                onResult(Result.failure(t))
            }
        })
    }
}
