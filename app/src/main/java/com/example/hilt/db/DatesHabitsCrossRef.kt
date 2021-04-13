package com.example.hilt.db

import androidx.room.Entity


@Entity(primaryKeys = ["date", "habit"])
data class DatesHabitsCrossRef(

    val date: String,
    val habit: String

)
