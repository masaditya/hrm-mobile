package com.hrmapps.data.model.attendance

data class CheckOutData (
    val id: Int,
    val user_id: Int,
    val clock_out_time: String,
    val auto_clock_out: String,
    val clock_out_ip: String,
    val half_day: String,
    val overwrite_attendance: String,
    val updated_at: String,
    val created_at: String,

)