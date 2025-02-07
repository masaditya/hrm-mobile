package com.hrmpandjiadhi.data.model.response

import com.hrmpandjiadhi.data.model.notice.Notice

data class NoticeResponse (
    val success: Boolean,
    val data: List<Notice>
)