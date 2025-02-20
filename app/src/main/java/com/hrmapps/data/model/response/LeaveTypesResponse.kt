package com.hrmapps.data.model.response

import com.hrmapps.data.model.leave.LeaveTypes

data class LeaveTypesResponse (
    val message: String,
    val data: List<LeaveTypes>
)