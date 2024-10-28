package com.hrmapps.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hrmapps.data.model.response.CheckInResponse
import com.hrmapps.data.repository.CheckInRepository
import kotlinx.coroutines.launch
import java.io.File

class CheckInViewModel(private val repository: CheckInRepository) : ViewModel() {
    private val _checkInResponse = MutableLiveData<CheckInResponse>()
    val checkInResponse: LiveData<CheckInResponse> get() = _checkInResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun checkIn(
        companyId: String,
        userId: String,
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
            try {
                val response = repository.checkIn(
                    companyId,
                    userId,
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
            } catch (e: Exception) {
                Log.e("CheckInViewModel", "Error during check-in: ${e.message}", e)
                _errorMessage.value = e.message
            }
        }
    }
}