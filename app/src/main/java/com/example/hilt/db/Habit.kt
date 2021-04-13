package com.example.hilt.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(

    @PrimaryKey(autoGenerate = false)
    val habit: String

)