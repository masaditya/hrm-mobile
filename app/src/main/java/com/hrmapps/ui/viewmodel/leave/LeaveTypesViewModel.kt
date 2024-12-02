package com.hrmapps.ui.viewmodel.leave

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.common.api.Response
import com.hrmapps.data.model.leave.LeaveTypes
import com.hrmapps.data.model.response.LeaveTypesResponse
import com.hrmapps.data.repository.leave.LeaveTypesRepository
import retrofit2.Call
import retrofit2.Callback

class LeaveTypesViewModel(private val leaveTypesRepository: LeaveTypesRepository) : ViewModel() {
    private val _leaveTypes = MutableLiveData<LeaveTypesResponse>()
    val leaveTypes: LiveData<LeaveTypesResponse> get() = _leaveTypes

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun getLeaveTypes(token: String) {
        _isLoading.value = true
        leaveTypesRepository.getLeaveTypes(token).enqueue(object : Callback<LeaveTypesResponse> {

            override fun onResponse(
                p0: Call<LeaveTypesResponse>,
                p1: retrofit2.Response<LeaveTypesResponse>
            ) {
                if (p1.isSuccessful) {
                    _isLoading.value = false
                    _leaveTypes.value = p1.body()
                } else {
                    _isLoading.value = false
                }
            }

            override fun onFailure(p0: Call<LeaveTypesResponse>, p1: Throwable) {
                _isLoading.value = false
                _errorMessage.value = p1.message
            }

        })
    }


}