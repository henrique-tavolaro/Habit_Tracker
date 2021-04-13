package com.example.hilt.db

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class HabitsWithDates(
    @Embedded
    val habit: Habit,
    @Relation(
        parentColumn = "habit",
        entityColumn = "date",
        associateBy = Junction(DatesHabitsCrossRef::class)
    )
    val dates: List<CalDate>

)
