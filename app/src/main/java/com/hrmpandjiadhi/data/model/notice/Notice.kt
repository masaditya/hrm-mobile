package com.hrmpandjiadhi.data.model.notice

data class Notice(
    val notice_id: Int,
    val user_id: Int,
    val read: Int,
    val heading: String,
    val date: String,
    val time: String
)
