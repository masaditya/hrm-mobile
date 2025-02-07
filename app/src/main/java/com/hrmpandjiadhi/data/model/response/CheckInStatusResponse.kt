package com.hrmpandjiadhi.data.model.response

import com.hrmpandjiadhi.data.model.attendance.CheckStatusAttendanceData

data class CheckInStatusResponse(
    val message: String,
    val data: CheckStatusAttendanceData
)
