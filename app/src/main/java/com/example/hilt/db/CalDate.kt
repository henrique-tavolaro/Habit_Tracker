package com.example.hilt.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "date")
data class CalDate(

    @PrimaryKey(autoGenerate = false)
    val date: String,
    val week: String
)
