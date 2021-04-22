package com.example.hilt.db

import androidx.room.*

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(calDate: CalDate)

    @Transaction
    @Query("SELECT * FROM date")
    suspend fun getAllDates(): List<CalDate>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Transaction
    @Query("SELECT * FROM habits")
    suspend fun getAllHabits(): List<Habit>

    @Transaction
    @Query("SELECT * FROM date WHERE date = :date")
    suspend fun getDateWithHabits(date: String): List<DatesWithHabits>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitWithDate(crossRef: DatesHabitsCrossRef)

    @Delete
    suspend fun deleteHabitWithDate(crossRef: DatesHabitsCrossRef)

    @Transaction
    @Query("SELECT * FROM habits WHERE habit = :habit")
    suspend fun getHabitsWithDates(habit: String): List<HabitsWithDates>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateDarkTheme(isDark: DarkTheme)

    @Transaction
    @Query("SELECT * FROM dark" )
    suspend fun getDarkModeList(): List<DarkTheme>

    @Transaction
    @Query("SELECT isDark FROM dark WHERE id = 1" )
    suspend fun getDarkMode(): Boolean
}

