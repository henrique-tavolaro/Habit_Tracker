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
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import com.example.hilt.db.CalDate
import com.example.hilt.db.DatesHabitsCrossRef
import com.example.hilt.db.DatesWithHabits
import com.example.hilt.db.Habit
import com.example.hilt.ui.theme.HiltTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

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
                    val isDeleteHabitDialogOpen = remember { mutableStateOf(false) }
                    val dateSelected = remember { mutableStateOf(newCurrDate) }

                    val historyToggle = remember { mutableStateOf(false) }
                    val radioOptions = listOf("Last 7 days", "Last 30 days")
                    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }

//                    val seven = Calendar.getInstance()
//                    seven.add(Calendar.DATE, -7)
//                    Log.d("seven1", seven.toString())
//                    Log.d("seven1", sdf.format(seven.time).toString())
//
//                    val listSeven = mutableListOf<String>()
//                    viewModel.getHabitsWithDates("sex")
//                    val habitsWithHabits = viewModel.habitsWithDates.value
//                    Log.d("seven3", habitsWithHabits.toString())
//
//                    if(habitsWithHabits.contains(sdf.format(seven.time))){
//                        listSeven.add(sdf.format(seven.time))
//                    }
//
//                    while(sdf.format(seven.time) != sdf.format(currDate.time)){
//                        if(habitsWithHabits.isNotEmpty() &&
//                            habitsWithHabits[0].dates.contains(CalDate(sdf.format(seven.time), "Thu"))){
//                            listSeven.add(sdf.format(seven.time))
//                        }
//                        Log.d("seven2", sdf.format(seven.time))
//                        seven.add(Calendar.DATE, plus)
//                    }
//
//                    Log.d("seven", listSeven.size.toString())


                    if (dateList.isNotEmpty()) {
                        val date = dateList[dateList.size - 1].date
                        if (date != sdf.format(currDate.time)) {
                            while (date !=
                                sdf.format(previous.time)
                            ) {
                                Log.d("date", sdf.format(previous.time))
                                dateList[dateList.size - 1].date?.let { Log.d("date2", it) }
                                previous.add(Calendar.DATE, amount)
                            }
                            val currTime = sdf.format(currDate.time).toString()
                            while (sdf.format(previous.time).toString() != sdf.format(currDate.time)
                                    .toString()
                            ) {
                                if (date != sdf.format(previous.time))
                                    viewModel.insertDate(
                                        CalDate(
                                            sdf.format(previous.time),
                                            week.format(previous.time)
                                        )
                                    )

                                previous.add(Calendar.DATE, plus).toString()
                            }
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
                                navigationIcon = {
                                    IconButton(onClick = {
                                        historyToggle.value = !historyToggle.value

                                    }) {
                                        Icon(Icons.Default.History, contentDescription = null)
                                    }
                                },
                                title = {
                                    Text(text = "Babe's Habit Tracker")
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
                        val listState = rememberLazyListState()
                        val coroutineScope = rememberCoroutineScope()
                        val lastIndex = dateList.size - 1


                        Column(Modifier.padding(8.dp)) {
                            if (!historyToggle.value) {
                                LazyRow(
                                    modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp, ),
                                    contentPadding = PaddingValues(start = 50.dp, end = 50.dp),
                                    state = listState
                                ) {
                                    items(items = dateList) { date ->
                                        coroutineScope.launch {
                                            listState.animateScrollToItem(lastIndex)
                                        }
                                        DateCard(
                                            date = date,
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
                            } else {
                                Card(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth()
                                        .height(80.dp),
                                    elevation = 8.dp,
                                    backgroundColor = MaterialTheme.colors.primary
                                ) {
                                    Column {

                                        Text(
                                            modifier = Modifier
                                                .padding(top = 8.dp)
                                                .fillMaxWidth(),
                                            text = "Check your stats",
                                            textAlign = TextAlign.Center
                                        )
                                        SimpleRadioButtonComponent(
                                            radioOptions,
                                            selectedOption,
                                            onOptionSelected,
                                            viewModel,
                                            sdf,
                                            week,
                                            currDate
                                        )
                                    }
                                }
                            }

                            Divider(
                                modifier = Modifier.padding(8.dp),
                                thickness = 1.dp,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.padding(16.dp))
                            LazyColumn(
                                Modifier.padding(8.dp),
                                contentPadding = PaddingValues(bottom = 56.dp)
                            ) {
                                items(items = habitList) { habit ->
                                    HabitCard(
                                        habit = habit,
                                        viewModel,
                                        dateSelected.value,
                                        datesWithHabits,
                                        isDeleteHabitDialogOpen,
                                        selectedOption,
                                        historyToggle,
                                        isDark
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
fun SimpleRadioButtonComponent(
    radioOptions: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    viewModel: UserViewModel,
    sdf: SimpleDateFormat,
    week: SimpleDateFormat,
    currDate: Calendar
) {
    Column {
        Row {
            radioOptions.forEach { text ->
                Row(
                    Modifier
                        .selectable(
                            selected = (text == selectedOption),
                            onClick = {
                                onOptionSelected(text)

                                val seven = Calendar.getInstance()

                                if (text == "Last 7 days") {
                                    seven.add(Calendar.DATE, -7)
                                } else {
                                    seven.add(Calendar.DATE, -30)
                                }
                                Log.d("seven1", seven.toString())
                                Log.d(
                                    "seven1",
                                    sdf
                                        .format(seven.time)
                                        .toString()
                                )

                                val listSeven = mutableListOf<String>()

                                val habitsList: List<Habit> = viewModel.habitList.value
                                viewModel.getAllHabits()
                                for (habit in habitsList) {
                                    viewModel.getHabitsWithDates(habit.habit)
                                }


                                val habitsWithHabits = viewModel.habitsWithDates.value
                                Log.d("seven3", habitsWithHabits.toString())

                                if (habitsWithHabits.contains(sdf.format(seven.time))) {
                                    listSeven.add(sdf.format(seven.time))
                                }
                                val plus = 1
                                while (sdf.format(seven.time) != sdf.format(currDate.time)) {
                                    if (habitsWithHabits.isNotEmpty() &&
                                        habitsWithHabits[0].dates.contains(
                                            CalDate(
                                                sdf.format(seven.time),
                                                week.format(seven.time)
                                            )
                                        )
                                    ) {
                                        listSeven.add(sdf.format(seven.time))
                                    }
                                    Log.d("seven2", sdf.format(seven.time))
                                    seven.add(Calendar.DATE, plus)
                                }
                                Log.d("seven", listSeven.size.toString())

                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {

                    RadioButton(
                        selected = (text == selectedOption),
                        modifier = Modifier.padding(all = Dp(value = 8F)),
                        onClick = {
                            onOptionSelected(text)
                        }
                    )
                    Text(
                        text = text,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun HabitCard(
    habit: Habit,
    viewModel: UserViewModel,
    calDate: String,
    dateHabit: List<DatesWithHabits>,
    isDialogOpen: MutableState<Boolean>,
    selectedOption: String,
    historyToggle: MutableState<Boolean>,
    isDark: Boolean
) {


    AlertDialogDeleteHabit(isDialogOpen, viewModel, habit)
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .padding(4.dp)
                .weight(1f),
            elevation = 4.dp,
            shape = CircleShape
        ) {
            IconButton(
                onClick = {
//                          isDialogOpen.value = true
                    viewModel.deleteHabit(habit)
                    viewModel.getAllHabits()
                },
            ) {
                Icon(Icons.Filled.Delete, contentDescription = null)
            }
        }
        var elevation = remember { mutableStateOf(1.dp) }

        if (dateHabit.isNotEmpty()) {

            val dateWithHabit = dateHabit[0]
            val dateHasHabit = dateWithHabit.habits.contains(Habit(habit.habit))

            var checked = false
            if (dateHasHabit) {
                checked = true
            }

            val color by animateColorAsState(
                if(checked) MaterialTheme.colors.primaryVariant
                else MaterialTheme.colors.surface
            )
            Card(
                modifier = Modifier
                    .padding(4.dp)
                    .weight(if (historyToggle.value) 3f else 4f)
                    .toggleable(
                        value = checked,
                        onValueChange = {
                            if (it) {
                                viewModel.insertHabitWithDate(
                                    DatesHabitsCrossRef(
                                        calDate,
                                        habit.habit
                                    )
                                )
                                viewModel.getDatesWithHabits(calDate)

                            } else {
                                viewModel.deleteHabitWithDate(
                                    DatesHabitsCrossRef(
                                        calDate,
                                        habit.habit
                                    )
                                )
                                viewModel.getDatesWithHabits(calDate)
                            }
                        }
                    ),
                backgroundColor = if(isDark) color else Color.White,
                elevation = elevation.value,
                shape = CircleShape,

                ) {

                Crossfade(targetState = checked) {

                    if (checked) {
                        elevation.value = 4.dp
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            text = habit.habit,
                            style = MaterialTheme.typography.h6
                        )
                    } else {
                        elevation.value = 1.dp
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            text = habit.habit,
                            color = Color.LightGray,
                            style = MaterialTheme.typography.h6
                        )
                    }

                }
            }
        } else {
            Card(
                modifier = Modifier
                    .padding(4.dp)
                    .weight(if (historyToggle.value) 3f else 4f)
                    .toggleable(
                        value = false,
                        onValueChange = {
                            if (it) {
                                viewModel.insertHabitWithDate(
                                    DatesHabitsCrossRef(
                                        calDate,
                                        habit.habit
                                    )
                                )
                                viewModel.getDatesWithHabits(calDate)

                            } else {
                                viewModel.deleteHabitWithDate(
                                    com.example.hilt.db.DatesHabitsCrossRef(
                                        calDate,
                                        habit.habit
                                    )
                                )
                                viewModel.getDatesWithHabits(calDate)
                            }
                        }
                    ),
                elevation = elevation.value,
                shape = CircleShape
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    text = habit.habit,
                    style = MaterialTheme.typography.h6
                )
            }
        }




        if (historyToggle.value) {
            Card(
                modifier = Modifier
                    .padding(4.dp)
                    .weight(2f),
                elevation = 4.dp,
                shape = CircleShape
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    text = if (selectedOption == "Last 7 days") "5 / 7" else "20 / 30",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h6
                )
            }
        } else {
//            if (dateHabit.isNotEmpty()) {
//                Card(
//                    modifier = Modifier
//                        .padding(4.dp)
//                        .weight(1f),
//                    elevation = 4.dp,
//                    shape = CircleShape
//                ) {
//
//                    val dateWithHabit = dateHabit[0]
//                    val dateHasHabit = dateWithHabit.habits.contains(Habit(habit.habit))
//
//                    var checked = false
//                    if (dateHasHabit) {
//                        checked = true
//                    }
//                    val color by animateColorAsState(
//                        if (checked) colorResource(id = R.color.checkGreen) else
//                            MaterialTheme.colors.surface.copy(0.1f)
//                    )
//                    IconToggleButton(
//                        modifier = Modifier
//                            .background(color),
//                        checked = checked,
//                        onCheckedChange = {
//                            if (it) {
//                                viewModel.insertHabitWithDate(
//                                    DatesHabitsCrossRef(
//                                        calDate,
//                                        habit.habit
//                                    )
//                                )
//                                viewModel.getDatesWithHabits(calDate)
//
//                            } else {
//                                viewModel.deleteHabitWithDate(
//                                    DatesHabitsCrossRef(
//                                        calDate,
//                                        habit.habit
//                                    )
//                                )
//                                viewModel.getDatesWithHabits(calDate)
//                            }
//                        }
//                    ) {
//                        Crossfade(targetState = checked) {
//
//                            if (checked)
//                                Icon(
//                                    Icons.Filled.Check,
//                                    contentDescription = null,
//                                    tint = Color.White,
//                                )
//                            else
//                                Icon(
//                                    Icons.Filled.Clear,
//                                    contentDescription = null,
//                                    tint = Color.LightGray
//                                )
//                        }
//                    }
//                }
//            }
            Card(
                modifier = Modifier
                    .padding(4.dp)
                    .weight(1f),
                elevation = 4.dp,
                shape = CircleShape
            ) {

                IconButton(
                    onClick = { /*TODO*/ },
                ) {
                    Icon(Icons.Filled.DateRange, contentDescription = null)
                }
            }

        }


    }
}

@Composable
fun AlertDialogDeleteHabit(
    isDialogOpen: MutableState<Boolean>,
    viewModel: UserViewModel,
    habit: Habit
) {
    if (isDialogOpen.value) {
        AlertDialog(onDismissRequest = {
            isDialogOpen.value = false
        },
            title = {
                Text(text = "Are you sure you want to delete this habit?")
            },
            dismissButton = {
                Button(onClick = {
                    isDialogOpen.value = false
                }) {
                    Text(text = "CANCEL")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteHabit(habit)
                        viewModel.getAllHabits()
                    }) {
                    Text(text = "CONFIRM")
                }
            }
        )
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
fun HabitSwitch(
    viewModel: UserViewModel,
    habit: String,
    calDate: String,
    dateHabit: List<DatesWithHabits>
) {

    if (dateHabit.isNotEmpty()) {
        val test = dateHabit[0]
        val test2 = test.habits.contains(Habit(habit))
        Log.d("test2a", test.toString())
        Log.d("test2b", test2.toString())


        var test3 = false
        if (test2) {
            test3 = true
        }
        Text(text = test2.toString())
        Switch(
            checked = test3,
            onCheckedChange = {
                if (it) {
                    viewModel.insertHabitWithDate(DatesHabitsCrossRef(calDate, habit))
                    viewModel.getDatesWithHabits(calDate)

                } else {
                    viewModel.deleteHabitWithDate(DatesHabitsCrossRef(calDate, habit))
                    viewModel.getDatesWithHabits(calDate)
                }
            },
        )
    }
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
            .padding(top = 4.dp, start = 1.dp)
    ) {
        Card(
            modifier = Modifier
                .padding(top = 8.dp)

                .toggleable(
                    value = isSelected,
                    onValueChange = { onSelectedDateChanged(date.date) }
                ),
            shape = RoundedCornerShape(6.dp),
            elevation = 8.dp,
            backgroundColor = if (isSelected) MaterialTheme.colors.secondary
            else MaterialTheme.colors.primary
        ) {
            if (isSelected) {
                viewModel.getDatesWithHabits(date.date)
                Log.d("card4", datesWithHabits.toString())
            }
            Column(modifier = Modifier.padding(4.dp)) {

                Text(modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 4.dp),
                    text = date.week,
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center
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

