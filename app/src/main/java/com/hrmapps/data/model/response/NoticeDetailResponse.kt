package com.hrmapps.data.model.response

import com.hrmapps.data.model.notice.DataNotice

data class NoticeDetailResponse(
    val success: Boolean,
    val data: DataNotice
)