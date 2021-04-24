package com.example.hilt.composables

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.hilt.UserViewModel
import java.text.SimpleDateFormat
import java.util.*


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
                            onClick = {}
                        )
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    RadioButton(
                        selected = (text == selectedOption),
                        modifier = Modifier.padding(all = Dp(value = 8F)),
                        onClick = {
                            onOptionSelected(text)
                            viewModel.habitStats(text, sdf, week, currDate)
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
