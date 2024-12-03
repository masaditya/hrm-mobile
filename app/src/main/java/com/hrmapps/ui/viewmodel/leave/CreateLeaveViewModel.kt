package com.hrmapps.ui.viewmodel.leave

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hrmapps.data.model.response.CreateLeaveResponse
import com.hrmapps.data.repository.leave.CreateLeaveRepository
import kotlinx.coroutines.launch
import java.io.File

class CreateLeaveViewModel(private val repository: CreateLeaveRepository) : ViewModel() {

    private val _Create_leaveResponse = MutableLiveData<CreateLeaveResponse>()
    val createLeaveResponseLiveData: LiveData<CreateLeaveResponse> = _Create_leaveResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessageLiveData: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoadingLiveData: LiveData<Boolean> = _isLoading


    fun createLeave(
        token: String,
        companyId: String,
        userId: String,
        leaveType: String,
        startDate: String,
        endDate: String,
        reason: String,
        addedBy: String,
        file: File?
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = repository.createLeave(
                    token,
                    companyId,
                    userId,
                    leaveType,
                    startDate,
                    endDate,
                    reason,
                    addedBy,
                    file
                )
                _isLoading.value = false

                response.onSuccess {
                    _Create_leaveResponse.value = it
                }.onFailure { throwable ->
                    _errorMessage.value = throwable.message ?: "An unknown error occurred"
                }

            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.message ?: "An unknown error occurred"
            }
        }
    }
}