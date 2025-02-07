package com.hrmpandjiadhi.ui.viewmodel.patroli

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hrmpandjiadhi.data.model.response.CheckPointResponse
import com.hrmpandjiadhi.data.repository.patroli.GetPatrolTypesRepository
import kotlinx.coroutines.launch

class GetPatrolViewModel(private val getPatrolTypesRepository: GetPatrolTypesRepository) : ViewModel() {

    private val _patrolTypes = MutableLiveData<CheckPointResponse?>()
    val patrolTypes: LiveData<CheckPointResponse?> get() = _patrolTypes

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun getPatrolTypes(token: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            getPatrolTypesRepository.getPatrolTypes(token) { result ->
                _isLoading.value = false
                result.onSuccess { response ->
                    _patrolTypes.value = response
                }.onFailure { exception ->
                    Log.e("GetPatrolViewModel", "Error fetching patrol types: ${exception.message}", exception)
                    _errorMessage.value = exception.message
                }
            }
        }
    }

}
