package com.hrmpandjiadhi.ui.viewmodel.leave

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hrmpandjiadhi.data.model.response.CreateLeaveResponse
import com.hrmpandjiadhi.data.repository.leave.CreateLeaveRepository
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


                response.onSuccess { it ->
                    if (it.success) {
                        _Create_leaveResponse.value = it
                        _isLoading.value = false
                    }else{
                        _errorMessage.value = it.message
                        _isLoading.value = false
                    }
                }.onFailure { throwable ->
                    _errorMessage.value = "Error: ${throwable.message ?: "Unknown error"}"
                    Log.e("CreateLeaveViewModel", "Error creating leave", throwable)
                    _isLoading.value = false
                }

            } catch (e: Exception) {
                _isLoading.value = false
                Log.e("CreateLeaveViewModel", "Error creating leave", e)
            }
        }
    }
}