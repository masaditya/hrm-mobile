package com.hrmapps.data.model.response

import com.hrmapps.data.model.attendance.Link
import com.hrmapps.data.model.leave.Leave

data class LeaveListResponse(
    val last_page_url: String?,
    val links: List<Link>,
    val data: List<Leave>,
    val current_page: Int,
    val last_page: Int,
    val total: Int
)

