package com.hrmapps.data.model.response

import com.hrmapps.data.model.notice.Notice

data class NoticeResponse (
    val success: Boolean,
    val data: List<Notice>
)