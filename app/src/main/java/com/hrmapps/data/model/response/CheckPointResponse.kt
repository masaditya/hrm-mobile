package com.hrmapps.data.model.response

import com.hrmapps.data.model.patroli.CheckPoint

data class CheckPointResponse (
    val message: String,
    val data: List<CheckPoint>
)