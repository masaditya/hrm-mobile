package com.hrmpandjiadhi.data.model.notice

data class DataNotice(
    val notice_id: Int,
    val to: String,
    val heading: String,
    val description: String,
    val added_by: String,
    val date: String,
    val time: String,
    val department: String,
    val filename: String,
    val hashname: String,
    val google_url: String?,
    val dropbox_link: String?,
    val external_link: String?,
    val external_link_name: String?,
    val user_id: Int,
    val read: Int
)