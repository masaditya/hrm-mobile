package com.hrmapps.data.model.response

import com.hrmapps.data.model.attendance.CheckOutData

data class CheckOutResponse (
    val message: String,
    val data: CheckOutData
)