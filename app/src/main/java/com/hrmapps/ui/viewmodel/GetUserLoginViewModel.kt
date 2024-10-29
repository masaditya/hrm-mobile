package com.hrmapps.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hrmapps.data.model.response.GetUserResponse
import com.hrmapps.data.repository.GetUserRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetUserLoginViewModel(private val repository: GetUserRepository) : ViewModel() {

    private val _userResponse = MutableLiveData<GetUserResponse>()
    val userResponse: LiveData<GetUserResponse> get() = _userResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun getUserLogin(token: String) {
        repository.getUser(token).enqueue(object : Callback<GetUserResponse> {
            override fun onResponse(
                call: Call<GetUserResponse>,
                response: Response<GetUserResponse>
            ) {
                if (response.isSuccessful) {
                    _userResponse.value = response.body()
                } else {
                    _error.value = "Error: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                _error.value = "Failure: ${t.message}"
            }
        })
    }
}
