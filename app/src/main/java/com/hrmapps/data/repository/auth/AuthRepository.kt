package com.hrmapps.data.repository.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hrmapps.data.api.ApiService
import com.hrmapps.data.model.response.LoginResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository(private val apiService: ApiService) {
    fun login(email: String, password: String, androidId: String): Call<LoginResponse> {
        return apiService.login(email, password, androidId)
    }

    fun logout(token: String): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()

        apiService.logout("Bearer $token").enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    result.value = Result.success(true)
                } else {
                    result.value = Result.failure(Throwable("Logout failed"))
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                result.value = Result.failure(t)
            }
        })

        return result
    }
    fun changePassword(token: String, password: String, passwordConfirmation: String): MutableLiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()

        apiService.changePassword("Bearer $token", password, passwordConfirmation).enqueue(object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    result.value = Result.success(true)
                } else {
                    result.value = Result.failure(Throwable("Logout failed"))

                }
        }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                result.value = Result.failure(t)
            }
        })
        return result

    }


}