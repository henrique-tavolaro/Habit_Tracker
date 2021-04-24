package com.example.hilt.composables

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.example.hilt.UserViewModel
import com.example.hilt.db.CalDate
import com.example.hilt.db.Habit
import java.text.SimpleDateFormat
import java.util.*

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