package com.hrmpandjiadhi.data.model.response

import com.hrmpandjiadhi.data.model.auth.User

data class LoginResponse(
    val message: String,
    val token: String,
    val user: User
)
