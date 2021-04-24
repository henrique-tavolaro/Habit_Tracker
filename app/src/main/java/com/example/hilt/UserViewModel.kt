package com.example.hilt

import android.graphics.Insets.add
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.graphics.Insets.add
import androidx.core.view.OneShotPreDrawListener.add
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hilt.db.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {


    val selectedCategory: MutableState<String?> = mutableStateOf(null)

    val isDark: MutableState<Boolean> = mutableStateOf(false)

    fun toggleLightTheme() {
        isDark.value = !isDark.value
        viewModelScope.launch {
            repository.toggleDarkTheme(DarkTheme(1, isDark.value))
        }
    }


    fun getDarkTheme() {
        viewModelScope.launch {
            isDark.value = repository.getDarkTheme()
        }
    }

    fun onSelectedCategoryChanged(date: String) {
        selectedCategory.value = date
        getDatesWithHabits(date)
    }

    val calDateList: MutableState<List<CalDate>> = mutableStateOf(listOf())

    var dateScrollPosition: Int = -1

    fun onChangeScrollPosition(position: Int) {
        dateScrollPosition = position
    }


    val habitList: MutableState<List<Habit>> = mutableStateOf(listOf())

    val habitTextInput = mutableStateOf("")

    val darkEntity: MutableState<List<DarkTheme>> = mutableStateOf(listOf())

    fun getDarkModeList() {
        viewModelScope.launch {
            darkEntity.value = repository.getDarkThemeList()
        }
    }


    fun onHabitTextInputChange(text: String) {
        habitTextInput.value = text
    }

    init {
        getAllHabits()
        getAllDates()
    }

    val datesWithHabits: MutableState<List<DatesWithHabits>> = mutableStateOf(listOf())

    fun getDatesWithHabits(date: String) {
        viewModelScope.launch {
            datesWithHabits.value = repository.getDatesWithHabits(date)
        }
    }


    fun insertHabitWithDate(crossRef: DatesHabitsCrossRef) {
        viewModelScope.launch {
            repository.insertHabitWithDate(crossRef)
        }
    }

    fun deleteHabitWithDate(crossRef: DatesHabitsCrossRef) {
        viewModelScope.launch {
            repository.deleteHabitWithDate(crossRef)
        }
    }

    fun getAllHabits() {
        viewModelScope.launch {
            habitList.value = repository.getAllHabits()
        }
    }

    fun getAllDates() {
        viewModelScope.launch() {
            calDateList.value = repository.getAllDates()
        }
    }

    fun insertHabit(habit: Habit) {
        viewModelScope.launch {
            repository.insertHabit(habit)
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    fun insertDate(calDate: CalDate) {
        viewModelScope.launch {
            repository.insertDate(calDate)
        }
    }

    val habitsWithDates: MutableState<List<HabitsWithDates>> = mutableStateOf(listOf())

    fun habitsWithDates(habit: String) {
        viewModelScope.launch {
            habitsWithDates.value = repository.getHabitsWithDates(habit)
        }
    }


    fun getHabitsWithDates(
        habit: String,
        sdf: SimpleDateFormat,
        week: SimpleDateFormat,
        newDate: Calendar,
        currDate: Calendar
    ) {
        viewModelScope.launch {
            val habitsWithDates = repository.getHabitsWithDates(habit)
            Log.d("Log1", habitsWithDates.toString())
            val date = newDate
            val listSeven = mutableListOf<CalDate>()
            val plus = 1
            val tomorrow = Calendar.getInstance()
            tomorrow.add(Calendar.DATE, plus)
            Log.d("currdate", currDate.time.toString())

            Log.d("currdate", date.time.toString())
            Log.d("currdate", tomorrow.time.toString())
            val listCalDate = mutableListOf<CalDate>()
//            val caldate = CalDate(sdf.format(date.time), week.format(date.time))
//            Log.d("Log2", "$habit , $caldate")
            while (sdf.format(date.time) != sdf.format(currDate.time)) {
                date.add(Calendar.DATE, plus)
                listCalDate.add(CalDate(sdf.format(date.time), week.format(date.time)))

                Log.d("listDate", "${listCalDate.toString()}, ${listCalDate.size}")
                Log.d("Log3", "$habit , ${sdf.format(date.time)}")
            }
            if (listCalDate.size == 7) {
                date.add(Calendar.DATE, -7)
            } else {
                date.add(Calendar.DATE, -30)
            }
            Log.d("Log6", listCalDate.size.toString())
            Log.d("Log4", listCalDate.toString())
            for (calDate in listCalDate) {
                Log.d("Log5", habit)
                if (habitsWithDates[0].dates.contains(calDate)
                ) {
                    listSeven.add(calDate)
                }
            }
            Log.d("Log7", "$habit , $listSeven , ${listSeven.size}")
            habitsWithDatesListStats.value.add(
                HabitStats(habit, listSeven.size)
            )
            Log.d("Log8", habitsWithDatesListStats.value.toString())
//            historyToggle.value = !historyToggle.value
            toggle.value = !toggle.value
        }
    }

    val toggle = mutableStateOf(false)

    val historyToggle : MutableState<Boolean> = mutableStateOf(false)

    val habitsWithDatesListStats: MutableState<MutableList<HabitStats>> =
        mutableStateOf(mutableListOf())

    fun habitStats(
        text: String,
        sdf: SimpleDateFormat,
        week: SimpleDateFormat,
        currDate: Calendar
    ) {
        viewModelScope.launch {
            val period = Calendar.getInstance()


            if (text == "Last 7 days") {
                period.add(Calendar.DATE, -7)
            } else {
                period.add(Calendar.DATE, -30)
            }
            val habitsList: List<Habit> = habitList.value
            getAllHabits()

            if (habitsList.isNotEmpty()) {
                if (habitsWithDatesListStats.value.isEmpty()) {
                    for (habit in habitsList) {
                        getHabitsWithDates(
                            habit.habit, sdf, week, period, currDate
                        )
                    }
                } else {
                    habitsWithDatesListStats.value = mutableListOf()
                    for (habit in habitsList) {
                        getHabitsWithDates(
                            habit.habit, sdf, week, period, currDate
                        )
                    }
                }
            }
        }
    }
}






