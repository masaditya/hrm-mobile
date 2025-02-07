package com.hrmpandjiadhi.data.model.response

import com.hrmpandjiadhi.data.model.attendance.CheckInData

data class CheckInResponse(
    val message: String,
    val data: CheckInData
)
