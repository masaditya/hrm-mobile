package com.hrmapps.ui.viewmodel.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hrmapps.data.model.response.LoginResponse
import com.hrmapps.data.repository.auth.AuthRepository
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

    private val _changePasswordResult = MutableLiveData<Result<Boolean>>()
    val changePasswordResult: LiveData<Result<Boolean>> get() = _changePasswordResult



    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

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
                        _errorMessage.postValue("Login failed: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    _loginResponse.postValue(null)
                    _isLoading.postValue(false)
                    _errorMessage.postValue("Login failed: ${t.message}")

                }
            })
        }
    }

    fun logout(token: String) {
        _isLoading.value = true
        authRepository.logout(token).observeForever { result ->
            _logoutResult.value = result
            _isLoading.value = false
            if (result.isSuccess) {
                _errorMessage.value = "Logout successful"
            } else {
                _errorMessage.value = "Logout failed: ${result.exceptionOrNull()?.message}"
            }
        }
    }
    fun changePassword(token: String, password: String, passwordConfirmation: String) {
        _isLoading.value = true
        authRepository.changePassword(token, password, passwordConfirmation).observeForever { result ->
            _changePasswordResult.value = result
            _isLoading.value = false
            if (result.isSuccess) {
                _errorMessage.value = "Password updated successfully"
            } else {
                _errorMessage.value = "Password updated failed: ${result.exceptionOrNull()?.message}"
            }
        }
    }
}
