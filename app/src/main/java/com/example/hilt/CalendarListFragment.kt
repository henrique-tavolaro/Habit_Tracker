package com.example.hilt

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.hilt.composables.*
import com.example.hilt.db.*
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
                    viewModel.getDarkModeList()
                    val darkMode = viewModel.darkEntity.value

                    if (darkMode.isNotEmpty()) {
                        viewModel.getDarkTheme()
                    }

                    val habitDelete = remember { mutableStateOf(Habit("a")) }
                    val toggle = viewModel.toggle
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
                    val historyToggle = viewModel.historyToggle
                    val radioOptions = listOf("Last 7 days", "Last 30 days")
                    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }

                    if (dateList.isNotEmpty()) {
                        val date = dateList[dateList.size - 1].date
                        if (date != sdf.format(currDate.time)) {
                            while (date !=
                                sdf.format(previous.time)
                            ) {
                                dateList[dateList.size - 1].date?.let { Log.d("date2", it) }
                                previous.add(Calendar.DATE, amount)
                            }
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
                    viewModel.habitStats(selectedOption, sdf, week, currDate)
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
                                    Text(text = "Habit Tracker")
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
                            Crossfade(targetState = historyToggle) {
                                if (!historyToggle.value) {
                                    ExtendedFloatingActionButton(
                                        text = {
                                            if (dateList.isEmpty()) {
                                                Text(text = "Add a habit to start")
                                            } else {
                                                Text(text = "Add a habit")
                                            }
                                        },
                                        backgroundColor = MaterialTheme.colors.primary,
                                        icon = { Icon(Icons.Default.AddCircle, null) },
                                        onClick = { isDialogOpen.value = true }
                                    )
                                }
                            }
                        }
                    ) {
                        val listState = rememberLazyListState()
                        val coroutineScope = rememberCoroutineScope()
                        val lastIndex = dateList.size - 1

                        Column(Modifier.padding(8.dp)) {
                            if (!historyToggle.value) {
                                LazyRow(
                                    modifier = Modifier.padding(
                                        start = 16.dp,
                                        top = 8.dp,
                                        end = 16.dp,
                                    ),
                                    contentPadding = PaddingValues(start = 50.dp, end = 50.dp),
                                    state = listState
                                ) {
                                    val position = viewModel.dateScrollPosition
                                    items(items = dateList) { date ->
                                        if (position == -1) {
                                            coroutineScope.launch {
                                                listState.animateScrollToItem(lastIndex)
                                            }
                                        } else {
                                            coroutineScope.launch {
                                                listState.animateScrollToItem(position)
                                            }
                                        }
                                        DateCard(
                                            date = date,
                                            isSelected = selectedCategory == date.date,
                                            onSelectedDateChanged = {
                                                viewModel.onSelectedCategoryChanged(it)
                                                dateSelected.value = it
                                                viewModel.onChangeScrollPosition(listState.firstVisibleItemIndex)
                                            },
                                            viewModel = viewModel,
                                            datesWithHabits = datesWithHabits,
                                            isDark = isDark
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
                                color = if (isDark) MaterialTheme.colors.primaryVariant else Color.Black
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
                                        isDark,
                                        findNavController(),
                                        toggle,
                                        habitDelete
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
                            AlertDialogDeleteHabit(
                                isDeleteHabitDialogOpen,
                                viewModel,
                                habitDelete.value
                            )
                            Log.d("habit4", habitDelete.value.toString())
                        }
                    }
                }
            }
        }
    }
}

