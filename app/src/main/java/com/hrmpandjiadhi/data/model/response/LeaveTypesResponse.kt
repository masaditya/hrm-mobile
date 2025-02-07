package com.hrmpandjiadhi.data.model.response

import com.hrmpandjiadhi.data.model.leave.LeaveTypes

data class LeaveTypesResponse (
    val message: String,
    val data: List<LeaveTypes>
)