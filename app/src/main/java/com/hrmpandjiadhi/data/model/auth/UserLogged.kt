package com.hrmpandjiadhi.data.model.auth

data class UserLogged (
    val id_user: Int,
    val employee_id: String,
    val company_name: String,
    val name: String,
    val image: String,
    val email: String,
    val designation: String,
    val role: String,
)