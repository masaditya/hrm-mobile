package com.hrmapps.data.model

data class CheckInStatusData (
    val companyId: Int,
    val userId: Int,
    val clockInTime: String,
    val autoClockOut: Int,
    val clockInIp: String,
    val late: String,
    val latitude: String,
    val longitude: String,
    val workFromType: String,
    val overwriteAttendance: String,
    val photo: String
)