package com.hrmpandjiadhi.data.model.patroli

data class PatrolData(
    val id: Int,
    val name: String,
    val patrol_type_id: String,
    val description: String,
    val longitude: String,
    val latitude: String,
    val added_by: String,
    val image: String,
    val created_at: String,
    val updated_at: String
)