package com.hrmpandjiadhi.ui.viewmodel.attendance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hrmpandjiadhi.data.model.response.CheckInResponse
import com.hrmpandjiadhi.data.repository.attendance.CheckInRepository
import kotlinx.coroutines.launch
import java.io.File

class CheckInViewModel(private val repository: CheckInRepository) : ViewModel() {
    private val _checkInResponse = MutableLiveData<CheckInResponse?>()
    val checkInResponse: LiveData<CheckInResponse?> get() = _checkInResponse

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun checkIn(
        companyId: String,
        userId: String,
        locationId: String,
        clockInTime: String,
        autoClockOut: String,
        clockInIp: String,
        late: String,
        latitude: String,
        longitude: String,
        workFromType: String,
        overwriteAttendance: String,
        photo: File,
        token: String
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.checkIn(
                    companyId,
                    userId,
                    locationId,
                    clockInTime,
                    autoClockOut,
                    clockInIp,
                    late,
                    latitude,
                    longitude,
                    workFromType,
                    overwriteAttendance,
                    photo,
                    token
                )
                // Handling the Result
                result.onSuccess { response ->
                    _checkInResponse.value = response
                }.onFailure { exception ->
                    _errorMessage.value = exception.message
                }
            } catch (e: Exception) {
                // Catch any unhandled exceptions and show a message
                _errorMessage.value = "An unexpected error occurred: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
