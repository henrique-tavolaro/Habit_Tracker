package com.example.hilt

import com.example.hilt.db.*
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val userDao: UserDao
){

    suspend fun insertDate(calDate: CalDate) = userDao.insertUser(calDate)

    suspend fun getAllDates() = userDao.getAllDates()

    suspend fun insertHabit(habit: Habit) = userDao.insertHabit(habit)

    suspend fun deleteHabit(habit: Habit) = userDao.deleteHabit(habit)

    suspend fun getAllHabits() = userDao.getAllHabits()

    suspend fun getDatesWithHabits(date: String) = userDao.getDateWithHabits(date)

    suspend fun getHabitsWithDates(habit: String) = userDao.getHabitsWithDates(habit)

    suspend fun insertHabitWithDate(crossRef: DatesHabitsCrossRef) =
        userDao.insertHabitWithDate(crossRef)

    suspend fun deleteHabitWithDate(crossRef: DatesHabitsCrossRef) =
        userDao.deleteHabitWithDate(crossRef)

    suspend fun toggleDarkTheme(isDark: DarkTheme) = userDao.updateDarkTheme(isDark)

    suspend fun getDarkTheme() = userDao.getDarkMode()

    suspend fun getDarkThemeList() = userDao.getDarkModeList()
}