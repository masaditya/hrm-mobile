package com.hrmapps.data.model.leave

data class Leave(
    val company_id: Int,
    val user_id: Int,
    val leave_type_id: Int,
    val type_name: String,
    val duration: String,
    val leave_date: String,
    val reason: String,
    val status: String,
    val paid: Int,
    val over_utilized: Int,
    val added_by: Int
)