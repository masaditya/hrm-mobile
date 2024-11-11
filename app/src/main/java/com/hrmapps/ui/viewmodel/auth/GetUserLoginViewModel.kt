package com.hrmapps.ui.viewmodel.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hrmapps.data.model.response.GetUserResponse
import com.hrmapps.data.repository.auth.GetUserRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetUserLoginViewModel(private val repository: GetUserRepository) : ViewModel() {

    private val _userResponse = MutableLiveData<GetUserResponse>()
    val userResponse: LiveData<GetUserResponse> get() = _userResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getUserLogin(token: String) {
        _isLoading.value = true
        repository.getUser(token).enqueue(object : Callback<GetUserResponse> {
            override fun onResponse(
                call: Call<GetUserResponse>,
                response: Response<GetUserResponse>
            ) {
                if (response.isSuccessful) {
                    _userResponse.value = response.body()
                    _isLoading.value = false
                } else {
                    _error.value = "Error: ${response.message()}"
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                _error.value = "Failure: ${t.message}"
                _isLoading.value = false
            }
        })
    }
}
