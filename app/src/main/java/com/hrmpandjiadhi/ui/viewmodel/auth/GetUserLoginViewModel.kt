package com.hrmpandjiadhi.ui.viewmodel.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hrmpandjiadhi.data.model.response.GetUserResponse
import com.hrmpandjiadhi.data.repository.auth.GetUserRepository

class GetUserLoginViewModel(private val repository: GetUserRepository) : ViewModel() {

    private val _userResponse = MutableLiveData<GetUserResponse>()
    val userResponse: LiveData<GetUserResponse> get() = _userResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getUserLogin(token: String) {
        _isLoading.value = true
        repository.getUser(token) { result ->
            _isLoading.value = false
            result.onSuccess { userResponse ->
                _userResponse.value = userResponse
            }.onFailure { throwable ->
                _error.value = throwable.message
            }
        }
    }
}
