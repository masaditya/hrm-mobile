package com.hrmpandjiadhi.ui.viewmodel.attendance

import androidx.lifecycle.*
import com.hrmpandjiadhi.data.model.attendance.AttendanceData
import com.hrmpandjiadhi.data.repository.attendance.AttendanceRepository
import kotlinx.coroutines.launch

class AttendanceViewModel(private val repository: AttendanceRepository) : ViewModel() {

    private val _attendanceData = MutableLiveData<List<AttendanceData>>()
    val attendanceData: LiveData<List<AttendanceData>> get() = _attendanceData

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun fetchAttendanceData(
        token: String,
        workingFrom: String,
        userId: Int,
        page: Int = 1,
        limit: Int = 20
    ) {
        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            val result = repository.fetchAttendance(token, workingFrom, userId, page, limit)
            result.onSuccess { attendanceList ->
                _attendanceData.value = attendanceList
            }.onFailure { exception ->
                _error.value = exception.message
            }
            _loading.value = false
        }
    }

}
