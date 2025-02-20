package com.hrmapps.data.repository.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hrmapps.data.api.ApiService
import com.hrmapps.data.model.response.LoginResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository(private val apiService: ApiService) {

    fun login(email: String, password: String, androidId: String): LiveData<Result<LoginResponse>> {
        val result = MutableLiveData<Result<LoginResponse>>()

        apiService.login(email, password, androidId).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        result.value = Result.success(it)
                    } ?: run {
                        result.value = Result.failure(Throwable("Login failed: Empty response body"))
                    }
                } else {
                    val errorMessage = extractErrorMessage(response.errorBody())
                    result.value = Result.failure(Throwable(errorMessage))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                result.value = Result.failure(t)
            }
        })

        return result
    }

    fun logout(token: String): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()

        apiService.logout("Bearer $token").enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    result.value = Result.success(true)
                } else {
                    val errorMessage = extractErrorMessage(response.errorBody())
                    result.value = Result.failure(Throwable(errorMessage))
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                result.value = Result.failure(t)
            }
        })

        return result
    }

    fun changePassword(token: String, password: String, passwordConfirmation: String): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()

        apiService.changePassword("Bearer $token", password, passwordConfirmation).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    result.value = Result.success(true)
                } else {
                    val errorMessage = extractErrorMessage(response.errorBody())
                    result.value = Result.failure(Throwable(errorMessage))
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                result.value = Result.failure(t)
            }
        })

        return result
    }

    fun updateEmail(token: String, id: Int, newEmail: String): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()

        val requestBody = RequestBody.create(
            "application/x-www-form-urlencoded".toMediaTypeOrNull(),
            "email=$newEmail"
        )

        apiService.updateEmail("Bearer $token", id, requestBody).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    result.value = Result.success(true)
                } else {
                    val errorMessage = extractErrorMessage(response.errorBody())
                    result.value = Result.failure(Throwable(errorMessage))
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                result.value = Result.failure(t)
            }
        })

        return result
    }

    // Helper function to extract error message from response.errorBody()
    private fun extractErrorMessage(errorBody: ResponseBody?): String {
        return try {
            val errorJson = errorBody?.string()
            val jsonObject = JSONObject(errorJson ?: "{}")
            jsonObject.optString("message", "An unknown error occurred")
        } catch (e: Exception) {
            "An unknown error occurred"
        }
    }
}
