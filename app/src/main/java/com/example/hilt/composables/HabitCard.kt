package com.example.hilt.composables

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hilt.CalendarListFragmentDirections
import com.example.hilt.UserViewModel
import com.example.hilt.db.DatesHabitsCrossRef
import com.example.hilt.db.DatesWithHabits
import com.example.hilt.db.Habit
import com.example.hilt.db.HabitStats


@Composable
fun HabitCard(
    habit: Habit,
    viewModel: UserViewModel,
    calDate: String,
    dateHabit: List<DatesWithHabits>,
    isDialogOpen: MutableState<Boolean>,
    selectedOption: String,
    historyToggle: MutableState<Boolean>,
    isDark: Boolean,
    navController: NavController,
    toggle: MutableState<Boolean>,
    habitDelete: MutableState<Habit>
) {
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
                    habitDelete.value = habit
                    isDialogOpen.value = true
                },
            ) {
                Icon(Icons.Filled.Delete, contentDescription = null)
            }
        }
        val elevation = remember { mutableStateOf(1.dp) }

        if (dateHabit.isNotEmpty()) {
            val dateWithHabit = dateHabit[0]
            val dateHasHabit = dateWithHabit.habits.contains(Habit(habit.habit))

            var checked = false
            if (dateHasHabit) {
                checked = true
            }

            val color by animateColorAsState(
                if (checked) MaterialTheme.colors.primaryVariant
                else MaterialTheme.colors.surface
            )

            if (historyToggle.value) {
                Card(
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(3.5f),
                    backgroundColor = if (isDark) color else Color.White,
                    elevation = 4.dp,
                    shape = CircleShape,
                    ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        text = habit.habit,
                        style = MaterialTheme.typography.h6
                    )
                }
            } else {
                Card(
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(4f)
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
                    backgroundColor = if (isDark) color else Color.White,
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
            }
        } else {
            Card(
                modifier = Modifier
                    .padding(4.dp)
                    .weight(if (historyToggle.value) 3.5f else 4f),
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

        Crossfade(targetState = historyToggle.value) {
            if (historyToggle.value) {
                Card(
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(2f),
                    elevation = 4.dp,
                    shape = CircleShape
                ) {
                    val habitsWithDatesListStats = viewModel.habitsWithDatesListStats.value
                    var stats = HabitStats(habit.habit, 0)
                    for (habitStat in habitsWithDatesListStats) {
                        if (habitStat.habit == habit.habit) {
                            stats = HabitStats(habitStat.habit, habitStat.stat)
                        }
                    }

                    if(toggle.value){
                        Text(
                            text = "000",
                        color = Color.Transparent
                        )
                    }
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        text = if (selectedOption == "Last 7 days") {
                            if (habitsWithDatesListStats.isNotEmpty() &&
                                stats.habit == habit.habit
                            ) {
                                "${stats.stat} / 7"
                            } else {
                                "0 / 7"
                            }
                        } else {
                            if (habitsWithDatesListStats.isNotEmpty() &&
                                stats.habit == habit.habit
                            ) {
                                "${stats.stat} / 30"
                            } else {
                                "0 / 30"
                            }
                        },
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.h6
                    )
                }
            } else {
                Card(
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(1f),
                    elevation = 4.dp,
                    shape = CircleShape
                ) {
                    IconButton(
                        onClick = {
                            val action = CalendarListFragmentDirections
                                .actionCalendarListFragmentToCalendarFragment(habit.habit, isDark)
                            navController.navigate(action)
                        },
                    ) {
                        Icon(Icons.Filled.DateRange, contentDescription = null)
                    }
                }
            }
        }
    }
}