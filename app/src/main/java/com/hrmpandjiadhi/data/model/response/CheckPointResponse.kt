package com.hrmpandjiadhi.data.model.response

import com.hrmpandjiadhi.data.model.patroli.CheckPoint

data class CheckPointResponse (
    val message: String,
    val data: List<CheckPoint>
)