package com.hrmpandjiadhi.ui.viewmodel.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hrmpandjiadhi.data.model.response.LocationOfficeUserResponse
import com.hrmpandjiadhi.data.repository.auth.LocationOfficeUserRepository
import kotlinx.coroutines.launch

class LocationOfficeUserViewModel(private val repository: LocationOfficeUserRepository) : ViewModel() {

    private val _locationOfficeUser = MutableLiveData<LocationOfficeUserResponse?>()
    val locationOfficeUser: LiveData<LocationOfficeUserResponse?> get() = _locationOfficeUser

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun getLocationOfficeUser(token: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            repository.getLocationOfficeUser(token) { result ->
                _isLoading.value = false
                result.onSuccess { response ->
                    _locationOfficeUser.value = response
                }.onFailure { exception ->
                    Log.e("LocationOfficeUserViewModel", "Error fetching location office user: ${exception.message}", exception)
                    _errorMessage.value = exception.message
                }
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
