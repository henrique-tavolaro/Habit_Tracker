package com.example.hilt.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        CalDate::class,
        Habit::class,
        DatesHabitsCrossRef::class
    ], version = 1
)
abstract class UserDatabase : RoomDatabase() {
    abstract fun getDao(): UserDao
}
