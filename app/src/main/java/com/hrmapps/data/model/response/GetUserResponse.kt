package com.hrmapps.data.model.response

import com.hrmapps.data.model.User


data class GetUserResponse(
    val message: String,
    val data: User
)
