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

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun getCheckInStatus(token: String, userId: Int) {
        viewModelScope.launch {
            repository.getCheckInStatus(token, userId).enqueue(object : Callback<CheckInStatusResponse> {
                override fun onResponse(
                    call: Call<CheckInStatusResponse>,
                    response: Response<CheckInStatusResponse>
                ) {
                    if (response.isSuccessful) {
                        _checkInStatus.value = response.body()
                    } else {
                        _errorMessage.value = "Error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<CheckInStatusResponse>, t: Throwable) {
                    _errorMessage.value = "Failure: ${t.message}"
                }
            })
        }
    }
}
