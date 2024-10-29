package com.hrmapps.data.model.response

import android.service.autofill.UserData

data class GetUserResponse(
    val message: String,
    val data: UserData
)
