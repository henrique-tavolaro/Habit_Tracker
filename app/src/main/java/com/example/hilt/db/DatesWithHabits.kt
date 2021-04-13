package com.example.hilt.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

data class DatesWithHabits(
    @Embedded
    val date: CalDate,
    @Relation(
        parentColumn = "date",
        entityColumn = "habit",
        associateBy = Junction(DatesHabitsCrossRef::class)
    )
    val habits: List<Habit>

)
