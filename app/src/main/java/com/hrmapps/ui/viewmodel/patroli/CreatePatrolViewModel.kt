package com.hrmapps.ui.viewmodel.patroli

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hrmapps.data.model.response.CreatePatrolResponse
import com.hrmapps.data.repository.patroli.CreatePatrolRepository
import kotlinx.coroutines.launch
import java.io.File

class CreatePatrolViewModel(private val repository: CreatePatrolRepository) : ViewModel() {

    private val _createPatrolResponse = MutableLiveData<CreatePatrolResponse>()
    val createPatrolResponse: LiveData<CreatePatrolResponse> get() = _createPatrolResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun createPatrol(
        token: String,
        name: String,
        patrolTypeId: String,
        description: String,
        latitude: String,
        longitude: String,
        addedBy: String,
        photo: File
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.createPatrol(
                token,
                name,
                patrolTypeId,
                description,
                latitude,
                longitude,
                addedBy,
                photo
            )
            _isLoading.value = false

            result.onSuccess { response ->
                _createPatrolResponse.value = response

            }.onFailure { throwable ->
                _error.value = throwable.message ?: "An unknown error occurred"
            }
        }
    }
}
