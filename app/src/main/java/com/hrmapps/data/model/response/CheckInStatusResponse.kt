package com.hrmapps.data.model.response

import com.hrmapps.data.model.attendance.CheckStatusAttendanceData

data class CheckInStatusResponse(
    val message: String,
    val data: CheckStatusAttendanceData
)
