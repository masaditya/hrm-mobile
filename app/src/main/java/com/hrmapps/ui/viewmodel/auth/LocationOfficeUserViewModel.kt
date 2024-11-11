package com.hrmapps.ui.viewmodel.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hrmapps.data.model.response.LocationOfficeUserResponse
import com.hrmapps.data.repository.auth.LocationOfficeUserRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationOfficeUserViewModel(private val repository: LocationOfficeUserRepository) : ViewModel() {
    private val _locationOfficeUser = MutableLiveData<LocationOfficeUserResponse>()
    val locationOfficeUser: LiveData<LocationOfficeUserResponse> get() = _locationOfficeUser

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun getLocationOfficeUser(token: String) {
        _isLoading.value = true
        repository.getLocationOfficeUser(token).enqueue(object :
            Callback<LocationOfficeUserResponse> {
            override fun onResponse(call: Call<LocationOfficeUserResponse>, response: Response<LocationOfficeUserResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _locationOfficeUser.value = response.body()
                } else {
                    _errorMessage.value = "Error: ${response.code()}"
                }
            }

            override fun onFailure(p0: Call<LocationOfficeUserResponse>, p1: Throwable) {
                _isLoading.value = false
                _errorMessage.value = p1.message
            }

        })
    }
}