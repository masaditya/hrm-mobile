package com.hrmapps.ui.viewmodel.attendance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hrmapps.data.model.response.CheckOutResponse
import com.hrmapps.data.repository.attendance.CheckOutRepository

class CheckOutViewModel(private val repository: CheckOutRepository) : ViewModel() {

    private val _checkOutResponse = MutableLiveData<CheckOutResponse>()
    val checkOutResponse: LiveData<CheckOutResponse> = _checkOutResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun checkOut(
        token: String,
        id: Int,
        userId: Int,
        clockOutTime: String,
        autoClockOut: String,
        clockOutIp: String,
        halfDay: String
    ) {
        _isLoading.value = true
        repository.checkOut(
            token,
            id,
            userId,
            clockOutTime,
            autoClockOut,
            clockOutIp,
            halfDay
        ) { result ->
            _isLoading.value = false
            result.onSuccess { response ->
                _checkOutResponse.value = response
            }.onFailure { exception ->
                _error.value = exception.message
            }
        }
    }
}
