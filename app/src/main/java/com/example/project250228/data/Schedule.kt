package com.example.project250228.data

data class Schedule(
    val id: Int,
    val course: String,
    val date: String,
    var startTime: String,
    val endTime: String,
    val isolation: String
)
