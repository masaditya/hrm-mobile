package com.hrmapps.data.model.response

import com.hrmapps.data.model.patroli.PatrolData

data class CreatePatrolResponse (
    val success: Boolean,
    val data: PatrolData,
    val message: String
)