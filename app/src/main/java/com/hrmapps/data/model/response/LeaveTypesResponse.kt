package com.hrmapps.data.model.response

import com.hrmapps.data.model.patroli.CheckPoint

data class LeaveTypesResponse (
    val message: String,
    val data: List<CheckPoint>
)