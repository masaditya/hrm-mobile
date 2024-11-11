package com.hrmapps.data.model.response

import com.hrmapps.data.model.attendance.CheckInData

data class CheckInResponse(
    val message: String,
    val data: CheckInData
)
