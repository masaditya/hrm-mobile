package com.hrmapps.data.model.response

import com.hrmapps.data.model.auth.LocationOfficeUser

data class LocationOfficeUserResponse (
    val message: String,
    val data: LocationOfficeUser
)