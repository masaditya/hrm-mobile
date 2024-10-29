package com.hrmapps.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hrmapps.data.model.response.CheckInStatusResponse
import com.hrmapps.data.repository.CheckInStatusRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckInStatusViewModel(private val repository: CheckInStatusRepository) : ViewModel() {
    private val _checkInStatus = MutableLiveData<CheckInStatusResponse>()
    val checkInStatus: LiveData<CheckInStatusResponse> get() = _checkInStatus

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun getCheckInStatus(token: String, userId: Int) {
        repository.getCheckInStatus(token, userId).enqueue(object : Callback<CheckInStatusResponse> {
            override fun onResponse(
                call: Call<CheckInStatusResponse>,
                response: Response<CheckInStatusResponse>
            ) {
                if (response.isSuccessful) {
                    _checkInStatus.value = response.body()
                } else {
                    _error.value = "Error: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<CheckInStatusResponse>, t: Throwable) {
                _error.value = "Failure: ${t.message}"
            }
        })
    }
}
