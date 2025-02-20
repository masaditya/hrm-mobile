package com.hrmapps.data.repository.patroli

import com.hrmapps.data.api.ApiService
import com.hrmapps.data.model.response.CheckPointResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetPatrolTypesRepository(private val apiService: ApiService) {

    fun getPatrolTypes(token: String, onResult: (Result<CheckPointResponse>) -> Unit) {
        val call = apiService.getPatrolTypes("Bearer $token")

        call.enqueue(object : Callback<CheckPointResponse> {
            override fun onResponse(
                call: Call<CheckPointResponse>,
                response: Response<CheckPointResponse>
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

            override fun onFailure(call: Call<CheckPointResponse>, t: Throwable) {
                onResult(Result.failure(t))
            }
        })
    }
}
