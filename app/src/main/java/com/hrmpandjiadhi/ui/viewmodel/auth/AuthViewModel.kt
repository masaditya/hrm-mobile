package com.hrmpandjiadhi.ui.viewmodel.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hrmpandjiadhi.data.model.response.LoginResponse
import com.hrmpandjiadhi.data.repository.auth.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _loginResponse = MutableLiveData<LoginResponse?>()
    val loginResponse: LiveData<LoginResponse?> get() = _loginResponse

    private val _logoutResult = MutableLiveData<Result<Boolean>>()
    val logoutResult: LiveData<Result<Boolean>> get() = _logoutResult

    private val _changePasswordResult = MutableLiveData<Result<Boolean>>()
    val changePasswordResult: LiveData<Result<Boolean>> get() = _changePasswordResult

    private val _updateEmailResult = MutableLiveData<Result<Boolean>>()
    val updateEmailResult: LiveData<Result<Boolean>> get() = _updateEmailResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun login(email: String, password: String, androidId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            authRepository.login(email, password, androidId).observeForever { result ->
                _isLoading.value = false
                result.onSuccess { response ->
                    _loginResponse.value = response
                }.onFailure { exception ->
                    _errorMessage.value = exception.message
                    _loginResponse.value = null
                }
            }
        }
    }


    fun logout(token: String) {
        _isLoading.value = true
        authRepository.logout(token).observeForever { result ->
            _logoutResult.value = result
            _isLoading.value = false
            result.onSuccess {
                _errorMessage.value = "Logout successful"
            }.onFailure { exception ->
                _errorMessage.value = exception.message
            }

        }
    }
    fun changePassword(token: String, password: String, passwordConfirmation: String) {
        _isLoading.value = true
        authRepository.changePassword(token, password, passwordConfirmation).observeForever { result ->
            _changePasswordResult.value = result
            _isLoading.value = false
            result.onSuccess {
                _errorMessage.value = "Password updated successfully"
            }.onFailure { exception ->
                _errorMessage.value = exception.message
            }

        }
    }

    fun changeEmail(token: String, id: Int, newEmail: String) {
        _isLoading.value = true
        authRepository.updateEmail(token, id, newEmail).observeForever { result ->
            _updateEmailResult.value = result
            _isLoading.value = false
            result.onSuccess {
                _errorMessage.value = "Email updated successfully"
            }.onFailure { exception ->
                _errorMessage.value = exception.message
            }

        }
    }
}
