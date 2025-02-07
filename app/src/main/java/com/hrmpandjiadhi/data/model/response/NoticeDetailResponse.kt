package com.hrmpandjiadhi.data.model.response

import com.hrmpandjiadhi.data.model.notice.DataNotice

data class NoticeDetailResponse(
    val success: Boolean,
    val data: DataNotice
)