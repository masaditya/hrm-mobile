package com.hrmpandjiadhi.data.model.response

import com.hrmpandjiadhi.data.model.auth.UserLogged


data class GetUserResponse(
    val message: String,
    val data: UserLogged
)
