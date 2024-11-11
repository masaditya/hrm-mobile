package com.hrmapps.ui.viewmodel.attendance

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hrmapps.data.model.response.CheckInResponse
import com.hrmapps.data.repository.attendance.CheckInRepository
import kotlinx.coroutines.launch
import java.io.File

class CheckInViewModel(private val repository: CheckInRepository) : ViewModel() {
    private val _checkInResponse = MutableLiveData<CheckInResponse>()
    val checkInResponse: LiveData<CheckInResponse> get() = _checkInResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

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
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.checkIn(
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
                _checkInResponse.value = response.getOrThrow()
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                Log.e("CheckInViewModel", "Error during check-in: ${e.message}", e)
                _errorMessage.value = e.message
            }
        }
    }
}
