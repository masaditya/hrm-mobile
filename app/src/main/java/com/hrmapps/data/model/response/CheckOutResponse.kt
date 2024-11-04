package com.hrmapps.data.model.response

import com.hrmapps.data.model.CheckOutData

data class CheckOutResponse (
    val message: String,
    val data: CheckOutData
)