package com.example.hilt.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(

    val name: String? = null
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

}
