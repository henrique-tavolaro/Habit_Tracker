package com.example.hilt.composables

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hilt.UserViewModel
import com.example.hilt.db.CalDate
import com.example.hilt.db.DatesWithHabits

@SuppressLint("SimpleDateFormat")
@Composable
fun DateCard(
    date: CalDate,
    viewModel: UserViewModel,
    isSelected: Boolean = false,
    onSelectedDateChanged: (String) -> Unit,
    datesWithHabits: List<DatesWithHabits>,
    isDark: Boolean
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
            backgroundColor = if (isDark) {
                if (isSelected) MaterialTheme.colors.primaryVariant
                else MaterialTheme.colors.secondary
            } else {
                if (isSelected) MaterialTheme.colors.secondary
                else MaterialTheme.colors.primary
            }
        ) {
            if (isSelected) {
                viewModel.getDatesWithHabits(date.date)
                Log.d("card4", datesWithHabits.toString())
            }
            Column(modifier = Modifier.padding(4.dp)) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 4.dp),
                    text = date.week,
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                    color = if (isDark) {
                        if (isSelected) Color.Black else MaterialTheme.colors.primaryVariant
                    } else {
                        if (isSelected) Color.Black else Color.White
                    }
                )
                Text(
                    text = date.date,
                    fontSize = 16.sp,
                    color = if (isDark) {
                        if (isSelected) Color.Black else MaterialTheme.colors.primaryVariant
                    } else {
                        if (isSelected) Color.Black else Color.White
                    }
                )
            }
        }
        Divider(
            modifier = Modifier.padding(6.dp),
            thickness = 1.dp,
            color = Color.Black
        )
    }
}