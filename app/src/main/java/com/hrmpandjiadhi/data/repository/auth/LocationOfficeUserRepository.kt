package com.hrmpandjiadhi.data.repository.auth

import com.hrmpandjiadhi.data.api.ApiService
import com.hrmpandjiadhi.data.model.response.LocationOfficeUserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationOfficeUserRepository(private val apiService: ApiService) {

    fun getLocationOfficeUser(token: String, onResult: (Result<LocationOfficeUserResponse>) -> Unit) {
        val call = apiService.getLocationOfficeUser("Bearer $token")

        call.enqueue(object : Callback<LocationOfficeUserResponse> {
            override fun onResponse(
                call: Call<LocationOfficeUserResponse>,
                response: Response<LocationOfficeUserResponse>
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

            override fun onFailure(call: Call<LocationOfficeUserResponse>, t: Throwable) {
                onResult(Result.failure(t))
            }
        })
    }
}
