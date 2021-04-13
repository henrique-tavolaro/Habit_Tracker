package com.example.hilt

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import com.example.hilt.db.CalDate
import com.example.hilt.db.DatesHabitsCrossRef
import com.example.hilt.db.DatesWithHabits
import com.example.hilt.db.Habit
import com.example.hilt.ui.theme.HiltTheme
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CalendarListFragment : Fragment() {

    private val viewModel: UserViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val isDark = viewModel.isDark.value

                HiltTheme(
                    darkTheme = isDark
                ) {
                val datesWithHabits = viewModel.datesWithHabits.value
                val dateList = viewModel.calDateList.value
                val sdf = SimpleDateFormat("d MMM yy")
                val week = SimpleDateFormat("EEE")
                val currDate = Calendar.getInstance()

                currDate.get(Calendar.DATE)
                val newCurrDate = sdf.format(currDate.time).toString()
                val selectedCategory = viewModel.selectedCategory.value

                val previous = Calendar.getInstance()
                val amount = -1
                val plus = 1
                previous.add(Calendar.DATE, amount)

                val habitList = viewModel.habitList.value
                val habitTextField = viewModel.habitTextInput
                val isDialogOpen = remember { mutableStateOf(false) }

                val dateSelected = remember { mutableStateOf(newCurrDate) }

                if (dateList.isNotEmpty()) {
                    val date = dateList[dateList.size - 1].date
                    if (date != sdf.format(currDate.time)) {
                        while (date.toString() !=
                            sdf.format(previous.time)
                        ) {
                            Log.d("date", sdf.format(previous.time))
                            dateList[dateList.size - 1].date?.let { Log.d("date2", it) }
                            previous.add(Calendar.DATE, amount)
                        }
                        var previousTime = sdf.format(previous.time)
                        val currTime = sdf.format(currDate.time).toString()
                        while (sdf.format(previous.time).toString() != sdf.format(currDate.time)
                                .toString()
                        ) {
                            Log.d("currDate", currTime)
                            Log.d("date4", sdf.format(previous.time))

                            if (date != sdf.format(previous.time))
                                viewModel.insertDate(
                                    CalDate(
                                        sdf.format(previous.time),
                                        week.format(previous.time)
                                    )
                                )

                            previous.add(Calendar.DATE, plus).toString()
                        }
                        Log.d("date5", currDate.toString())
                        viewModel.insertDate(
                            CalDate(
                                sdf.format(currDate.time).toString(),
                                week.format(currDate.time).toString()
                            )
                        )
                    }
                    viewModel.getAllDates()
                }

                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(text = "Marcia's Habit Tracker")
                                },
                                actions = {
                                    IconButton(
                                        onClick = {
                                            viewModel.toggleLightTheme()
                                        }
                                    ) {
                                        Icon(Icons.Default.MoreVert, contentDescription = null)
                                    }
                                },
                            )
                        },
                        floatingActionButton = {
                            ExtendedFloatingActionButton(
                                text = {
                                    Text(text = "Add a habit")
                                },
                                backgroundColor = MaterialTheme.colors.primary,
                                icon = { Icon(Icons.Default.AddCircle, null) },
                                onClick = { isDialogOpen.value = true }
                            )
                        }
                    ) {

                        Column(Modifier.padding(8.dp)) {
                            LazyRow(modifier = Modifier.padding(horizontal = 8.dp),
                            contentPadding = PaddingValues(start = 50.dp, end = 50.dp)) {
                                items(items = dateList) { date ->
                                    DateCard(
                                        date = date,
//                                        onClick = {
//                                            dateSelected.value = date.date
//                                            viewModel.getDatesWithHabits(date.date)
//                                            isSelected.value = true
//                                        },
                                        isSelected = selectedCategory == date.date,
                                        onSelectedDateChanged = {
                                            viewModel.onSelectedCategoryChanged(it)
                                            dateSelected.value = it
                                        },
                                        viewModel = viewModel,
                                        datesWithHabits = datesWithHabits
                                    )
                                }
                            }
                            Divider(
                                modifier = Modifier.padding(8.dp),
                                thickness = 1.dp,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.padding(16.dp))
                            LazyColumn(Modifier.padding(8.dp)) {
                                items(items = habitList) { habit ->
                                    HabitCard(
                                        habit = habit,
                                        viewModel,
                                        dateSelected.value,
                                        datesWithHabits
                                    )
                                }
                            }

                            AlertDialogComponent(
                                isDialogOpen = isDialogOpen,
                                text = habitTextField,
                                viewModel = viewModel,
                                dateList = dateList,
                                sdf = sdf,
                                week = week,
                                previous = currDate
                            )
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun AlertDialogComponent(
    isDialogOpen: MutableState<Boolean>,
    text: MutableState<String>,
    viewModel: UserViewModel,
    dateList: List<CalDate>,
    sdf: SimpleDateFormat,
    week: SimpleDateFormat,
    previous: Calendar

) {

    if (isDialogOpen.value) {
        AlertDialog(
            onDismissRequest = { isDialogOpen.value = false },
            title = {
                Text(text = "Create a habit")
            },
            text = {
                OutlinedTextField(
                    value = text.value,
                    onValueChange = { newValue ->
                        viewModel.onHabitTextInputChange(newValue)
                    })
            },

            confirmButton = {
                Button(onClick = {
                    isDialogOpen.value = false
                    val habit = Habit(text.value)
                    viewModel.insertHabit(habit)
                    viewModel.getAllHabits()
                    text.value = ""

                    if (dateList.isEmpty()) {
                        viewModel.insertDate(
                            CalDate(
                                sdf.format(previous.time),
                                week.format(previous.time)
                            )
                        )
                        viewModel.getAllDates()
                    }

                }) {
                    Text(text = "CONFIRM")
                }
            },
            dismissButton = {
                Button(onClick = {
                    isDialogOpen.value = false
                }) {
                    Text(text = "CANCEL")
                }
            }
        )
    }
}


@Composable
fun HabitCard(
    habit: Habit,
    viewModel: UserViewModel,
    calDate: String,
    datesHabit: List<DatesWithHabits>
) {
    Card(
        modifier = Modifier
            .width(300.dp)
            .padding(4.dp),
        elevation = 4.dp
    ) {

        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = habit.habit,
                style = MaterialTheme.typography.h5
            )
            HabitSwitch(viewModel, habit.habit, calDate, datesHabit)
        }
    }
}

@Composable
fun HabitSwitch(
    viewModel: UserViewModel,
    habit: String,
    calDate: String,
    dateHabit: List<DatesWithHabits>
) {

    val state = remember { mutableStateOf(false) }
//    val state = viewModel.switchState

    Switch(
        checked = if (dateHabit.isNotEmpty()) {
            val test = dateHabit[0]
            test.habits.contains(Habit(habit))
            !state.value
        }  else state.value,
        onCheckedChange = {
            state.value = it
            if (it) {
                viewModel.insertHabitWithDate(DatesHabitsCrossRef(calDate, habit))
            } else {
                viewModel.deleteHabitWithDate(DatesHabitsCrossRef(calDate, habit))
            }
        },
    )
}

@SuppressLint("SimpleDateFormat")
@Composable
fun DateCard(
    date: CalDate,
//    onClick: () -> Unit,
    viewModel: UserViewModel,
    isSelected: Boolean = false,
    onSelectedDateChanged: (String) -> Unit,
    datesWithHabits: List<DatesWithHabits>
) {
    Column(
        modifier = Modifier
            .padding(top = 4.dp, bottom = 4.dp, start = 1.dp)
    ) {
        Card(
            modifier = Modifier
                .padding(vertical = 8.dp)

                .toggleable(
                    value = isSelected,
                    onValueChange = { onSelectedDateChanged(date.date) }
                ),
            shape = RoundedCornerShape(6.dp),
            elevation = 8.dp,
            backgroundColor = if (isSelected) Color.LightGray else MaterialTheme.colors.primary
        ) {if(isSelected){
            viewModel.getDatesWithHabits(date.date)
            Log.d("card4", datesWithHabits.toString())
        }
            Column(modifier = Modifier.padding(4.dp)) {

                Text(
                    text = date.week,
                    fontSize = 30.sp,
                    textAlign = TextAlign.Justify
                )
                Text(
                    text = date.date,
                    fontSize = 16.sp
                )
            }
        }
        Divider(
            modifier = Modifier.padding(8.dp),
            thickness = 1.dp,
            color = Color.Black
        )
    }
}
