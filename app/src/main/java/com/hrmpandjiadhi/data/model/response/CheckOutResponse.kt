package com.hrmpandjiadhi.data.model.response

import com.hrmpandjiadhi.data.model.attendance.CheckOutData

data class CheckOutResponse (
    val message: String,
    val data: CheckOutData
)