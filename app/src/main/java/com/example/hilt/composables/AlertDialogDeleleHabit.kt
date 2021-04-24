package com.example.hilt.composables

import android.util.Log
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.example.hilt.UserViewModel
import com.example.hilt.db.Habit


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
                        Log.d("habit2", habit.toString())
                        viewModel.deleteHabit(habit)
                        viewModel.getAllHabits()
                        isDialogOpen.value = false
                    }
                ) {
                    Text(text = "CONFIRM")
                }
            }
        )
    }
}






