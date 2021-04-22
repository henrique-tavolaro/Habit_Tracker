package com.example.hilt.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dark")
data class DarkTheme(

    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val isDark: Boolean

)
