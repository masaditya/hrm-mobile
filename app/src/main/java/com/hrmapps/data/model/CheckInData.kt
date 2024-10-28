package com.hrmapps.data.model

import java.io.File

data class CheckInData(
    val company_id: String,
    val user_id: String,
    val clock_in_time: String,
    val auto_clock_out: String,
    val clock_in_ip: String,
    val late: String,
    val latitude: String,
    val longitude: String,
    val work_from_type: String,
    val overwrite_attendance: String,
    val photo: String,
    val updated_at: String,
    val created_at: String,
    val id: Int
)