package com.hrmapps.ui.viewmodel.leave

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hrmapps.data.model.response.LeaveTypesResponse
import com.hrmapps.data.repository.leave.LeaveTypesRepository

class LeaveTypesViewModel(private val leaveTypesRepository: LeaveTypesRepository) : ViewModel() {

    private val _leaveTypes = MutableLiveData<LeaveTypesResponse>()
    val leaveTypes: LiveData<LeaveTypesResponse> get() = _leaveTypes

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun getLeaveTypes(token: String) {
        _isLoading.value = true
        leaveTypesRepository.getLeaveTypes(token) { result ->
            result.onSuccess { leaveTypesResponse ->
                _leaveTypes.value = leaveTypesResponse
                _isLoading.value = false
            }.onFailure { exception ->
                _errorMessage.value = "Error: ${exception.message}"
                _isLoading.value = false
            }
        }
    }
}
