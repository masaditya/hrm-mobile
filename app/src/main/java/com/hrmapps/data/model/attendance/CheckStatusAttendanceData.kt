package com.hrmapps.data.model.attendance

data class CheckStatusAttendanceData (
    val company_id: Int,
    val user_id: Int,
    val clock_in_time: String,
    val clock_out_time: String,
    val auto_clock_out: Int,
    val clock_in_ip: String,
    val clock_out_ip: String,
    val late: String,
    val latitude: String,
    val longitude: String,
    val work_from_type: String,
    val overwrite_attendance: String,
    val photo: String
)