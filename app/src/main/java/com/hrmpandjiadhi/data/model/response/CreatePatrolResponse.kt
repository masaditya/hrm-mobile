package com.hrmpandjiadhi.data.model.response

import com.hrmpandjiadhi.data.model.patroli.PatrolData

data class CreatePatrolResponse (
    val success: Boolean,
    val data: PatrolData,
    val message: String
)