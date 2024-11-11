package com.hrmapps.ui.viewmodel.attendance

import androidx.lifecycle.*
import com.hrmapps.data.model.attendance.AttendanceData
import com.hrmapps.data.repository.attendance.AttendanceRepository
import kotlinx.coroutines.launch

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
