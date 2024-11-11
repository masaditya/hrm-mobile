package com.hrmapps.data.model.response

import com.hrmapps.data.model.auth.User

data class LoginResponse(
    val message: String,
    val token: String,
    val user: User
)
