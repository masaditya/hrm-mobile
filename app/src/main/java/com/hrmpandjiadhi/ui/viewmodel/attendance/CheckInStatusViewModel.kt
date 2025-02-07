package com.hrmpandjiadhi.ui.viewmodel.attendance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hrmpandjiadhi.data.model.response.CheckInStatusResponse
import com.hrmpandjiadhi.data.repository.attendance.CheckInStatusRepository

class CheckInStatusViewModel(private val repository: CheckInStatusRepository) : ViewModel() {

    private val _checkInStatus = MutableLiveData<CheckInStatusResponse?>()
    val checkInStatus: LiveData<CheckInStatusResponse?> get() = _checkInStatus

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getCheckInStatus(token: String, userId: Int) {
        _isLoading.value = true
        _error.value = null

        repository.getCheckInStatus(token, userId) { result ->
            _isLoading.postValue(false)
            result.onSuccess { status ->
                _checkInStatus.postValue(status)
            }.onFailure { exception ->
                _error.postValue(exception.message)
            }
        }
    }

}
