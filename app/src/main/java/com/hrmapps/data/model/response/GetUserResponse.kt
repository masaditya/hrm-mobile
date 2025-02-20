package com.hrmapps.data.model.response

import com.hrmapps.data.model.auth.UserLogged


data class GetUserResponse(
    val message: String,
    val data: UserLogged
)
