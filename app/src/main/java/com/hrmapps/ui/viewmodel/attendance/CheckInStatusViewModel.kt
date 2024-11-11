package com.hrmapps.ui.viewmodel.attendance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hrmapps.data.model.response.CheckInStatusResponse
import com.hrmapps.data.repository.attendance.CheckInStatusRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckInStatusViewModel(private val repository: CheckInStatusRepository) : ViewModel() {
    private val _checkInStatus = MutableLiveData<CheckInStatusResponse>()
    val checkInStatus: LiveData<CheckInStatusResponse> get() = _checkInStatus

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getCheckInStatus(token: String, userId: Int) {
        _isLoading.value = true
        repository.getCheckInStatus(token, userId).enqueue(object : Callback<CheckInStatusResponse> {
            override fun onResponse(
                call: Call<CheckInStatusResponse>,
                response: Response<CheckInStatusResponse>
            ) {
                if (response.isSuccessful) {
                    _checkInStatus.value = response.body()
                    _isLoading.value = false
                } else {
                    _error.value = "Error: ${response.message()}"
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<CheckInStatusResponse>, t: Throwable) {
                _error.value = "Failure: ${t.message}"
                _isLoading.value = false
            }
        })
    }
}
