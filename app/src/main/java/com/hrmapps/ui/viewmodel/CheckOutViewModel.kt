package com.hrmapps.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hrmapps.data.model.response.CheckOutResponse
import com.hrmapps.data.repository.CheckOutRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        val call = repository.checkOut(token, id, userId, clockOutTime, autoClockOut, clockOutIp, halfDay)
        call.enqueue(object : Callback<CheckOutResponse> {
            override fun onResponse(call: Call<CheckOutResponse>, response: Response<CheckOutResponse>) {
                if (response.isSuccessful) {
                    _checkOutResponse.value = response.body()
                    _isLoading.value = false
                } else {
                    _error.value = "Failed: ${response.message()}"
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<CheckOutResponse>, t: Throwable) {
                _error.value = "Error: ${t.message}"
                _isLoading.value = false
            }
        })
    }
}