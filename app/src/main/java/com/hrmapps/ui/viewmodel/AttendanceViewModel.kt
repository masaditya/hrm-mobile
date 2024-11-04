package com.hrmapps.ui.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hrmapps.data.model.AttendanceData
import com.hrmapps.data.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AttendanceViewModel(private val repository: AttendanceRepository) : ViewModel() {

    private val _attendanceData = MutableLiveData<List<AttendanceData>>()
    val attendanceData: LiveData<List<AttendanceData>> get() = _attendanceData

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchAttendanceData(token: String, workingFrom: String, userId: Int, page: Int = 1, limit: Int = 20) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val attendanceList = repository.fetchAttendance(token, workingFrom, userId, page, limit)
                _attendanceData.value = attendanceList
                _loading.value = false
            } catch (e: Exception) {
                _error.value = "Error fetching attendance data: ${e.message}"
                _loading.value = false
            }
        }

    }



}
