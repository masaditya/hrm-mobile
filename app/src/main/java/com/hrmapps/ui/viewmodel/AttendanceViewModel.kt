package com.hrmapps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asLiveData
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hrmapps.data.model.AttendanceData
import kotlinx.coroutines.flow.Flow
import com.hrmapps.data.repository.AttendanceRepository

class AttendanceViewModel(private val repository: AttendanceRepository) : ViewModel() {

    fun getPagedAttendance(
        token: String,
        workingFrom: String,
        locationId: Int,
        userId: Int
    ): Flow<PagingData<AttendanceData>> {
        return repository.getPagedAttendance(token, workingFrom, locationId, userId)
            .cachedIn(viewModelScope)
    }
}
