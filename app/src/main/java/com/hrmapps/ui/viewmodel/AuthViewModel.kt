package com.hrmapps.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.hrmapps.data.model.response.LoginResponse
import com.hrmapps.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _loginResponse = MutableLiveData<LoginResponse?>()
    val loginResponse: LiveData<LoginResponse?> get() = _loginResponse

    private val _logoutResult = MutableLiveData<Result<Boolean>>()
    val logoutResult: LiveData<Result<Boolean>> get() = _logoutResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(email: String, password: String, androidId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            authRepository.login(email, password, androidId).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        _loginResponse.postValue(response.body())
                        _isLoading.postValue(false)
                    } else {
                        _loginResponse.postValue(null)
                        _isLoading.postValue(false)
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    _loginResponse.postValue(null)
                    _isLoading.postValue(false)
                }
            })
        }
    }

    fun logout(token: String) {
        _isLoading.value = true
        authRepository.logout(token).observeForever { result ->
            _logoutResult.value = result
            _isLoading.value = false
        }
    }
}
