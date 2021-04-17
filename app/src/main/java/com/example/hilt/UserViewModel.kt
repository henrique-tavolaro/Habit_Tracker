package com.example.hilt

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hilt.db.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {



    val selectedCategory: MutableState<String?> = mutableStateOf(null)

    val isDark = mutableStateOf(false)

    fun toggleLightTheme() {
        isDark.value = !isDark.value
    }
    fun onSelectedCategoryChanged(date: String){
        selectedCategory.value = date
        getDatesWithHabits(date)
    }



    val calDateList: MutableState<List<CalDate>> = mutableStateOf(listOf())

    val habitList: MutableState<List<Habit>> = mutableStateOf(listOf())

    val habitTextInput = mutableStateOf("")

    fun onHabitTextInputChange(text: String) {
        habitTextInput.value = text
    }

    init {
        getAllHabits()
        getAllDates()
    }

    val datesWithHabits : MutableState<List<DatesWithHabits>> = mutableStateOf(listOf())

    fun getDatesWithHabits(date: String){
        viewModelScope.launch {
            datesWithHabits.value = repository.getDatesWithHabits(date)

        }
    }

    val habitsWithDates : MutableState<List<HabitsWithDates>> = mutableStateOf(listOf())

    fun getHabitsWithDates(habit: String){
        viewModelScope.launch {
            habitsWithDates.value = repository.getHabitsWithDates(habit)
        }
    }

    fun insertHabitWithDate(crossRef: DatesHabitsCrossRef){
        viewModelScope.launch {
            repository.insertHabitWithDate(crossRef)
        }
    }

    fun deleteHabitWithDate(crossRef: DatesHabitsCrossRef){
        viewModelScope.launch {
            repository.deleteHabitWithDate(crossRef)
        }
    }

    fun getAllHabits(){
        viewModelScope.launch {
            habitList.value = repository.getAllHabits()
        }
    }

    fun getAllDates(){
        viewModelScope.launch() {
                calDateList.value = repository.getAllDates()
        }
    }

    fun insertHabit(habit: Habit) {
        viewModelScope.launch {
            repository.insertHabit(habit)
        }
    }

    fun deleteHabit(habit: Habit){
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    fun insertDate(calDate: CalDate) {
        viewModelScope.launch {
            repository.insertDate(calDate)
        }
    }

}