package com.hrmapps.ui.viewmodel.leave

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hrmapps.data.model.leave.Leave
import com.hrmapps.data.repository.leave.ListLeaveRepository
import kotlinx.coroutines.launch

class LeaveViewModel(private val repository: ListLeaveRepository) : ViewModel() {
    private val _leaveData = MutableLiveData<List<Leave>>()
    val leaveData: LiveData<List<Leave>> get() = _leaveData

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchLeaveData(token: String, userId: Int, page: Int = 1, limit: Int = 20) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val leaveList = repository.fetchLeave(token, page, limit, userId)
                _leaveData.value = leaveList
                _loading.value = false
            } catch (e: Exception) {
                _error.value = "Error fetching attendance data: ${e.message}"
                _loading.value = false
            }
        }

    }
}
