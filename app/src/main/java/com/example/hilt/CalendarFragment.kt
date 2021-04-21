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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import be.sigmadelta.calpose.Calpose
import be.sigmadelta.calpose.WEIGHT_7DAY_WEEK
import be.sigmadelta.calpose.model.CalposeActions
import be.sigmadelta.calpose.model.CalposeDate
import be.sigmadelta.calpose.model.CalposeWidgets
import be.sigmadelta.calpose.widgets.DefaultDay
import be.sigmadelta.calpose.widgets.MaterialHeader
import com.example.hilt.ui.theme.HiltTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class CalendarFragment : Fragment() {

    private val viewModel: UserViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val isDark = viewModel.isDark.value
                val args: CalendarFragmentArgs by navArgs()
//
                HiltTheme(
                    darkTheme =
                    if (args.isDark) !isDark else isDark
                ) {
                      Log.d("args", args.habit)
//
                    val datesList: MutableList<CalposeDate> = mutableListOf()
//
                    val calendar = Calendar.getInstance()

                    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yy, MM")

                    val formatYearMonth = SimpleDateFormat("yy, MM")
                    val formatDay = SimpleDateFormat("d")
                    val sdf = SimpleDateFormat("dd MMM yy")
                    viewModel.habitsWithDates(args.habit)
                    val habitsWithDates = viewModel.habitsWithDates.value
                    if (habitsWithDates.isNotEmpty()) {
                        for (habitDate in habitsWithDates[0].dates!!) {
                            Log.d("habitdate", habitDate.date)
                            calendar.time = sdf.parse(habitDate.date!!)
                            val yearMonth = formatYearMonth.format(calendar.time)

                            val year = YearMonth.parse(yearMonth, formatter)
                            val week = habitDate.week
                            var we: DayOfWeek = DayOfWeek.MONDAY
                            when (week) {
                                "Mon" -> we = DayOfWeek.MONDAY
                                "Tue" -> we = DayOfWeek.TUESDAY
                                "Wed" -> we = DayOfWeek.WEDNESDAY
                                "Thu" -> we = DayOfWeek.THURSDAY
                                "Fri" -> we = DayOfWeek.FRIDAY
                                "Sat" -> we = DayOfWeek.SATURDAY
                                "Sun" -> we = DayOfWeek.SUNDAY
                            }


                            val day = formatDay.format(calendar.time).toInt()
                            Log.d("ze", day.toString())
                            Log.d("ze", we.toString())
                            Log.d("ze", year.toString())
                            val calpose = CalposeDate(day, we, year)
                            datesList.add(calpose)

                            Log.d("ze", calpose.toString())
                        }
                        Log.d("habitdate", datesList.toString())

                    }


                    Log.d("habitdate2", datesList.toString())

                    Scaffold(
                        topBar = {
                            TopAppBar(
                                navigationIcon = {
                                    IconButton(onClick = {
                                        findNavController()
                                            .navigate(R.id.action_calendarFragment_to_calendarListFragment)
                                    }) {
                                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                                    }
                                },
                                title = {
                                    Text(text = args.habit)
                                },
                                actions = {
                                    IconButton(
                                        onClick = {
                                            viewModel.toggleLightTheme()
                                        }
                                    ) {
                                        Icon(Icons.Default.MoreVert, contentDescription = null)
                                    }
                                }
                            )
                        }
                    ) {
                        MaterialPreview(datesList, isDark)
                    }
                }
            }
        }
    }
}

@Composable
fun MaterialPreview(dateList: MutableList<CalposeDate>, isDark: Boolean) {

    val monthFlow = MutableStateFlow(YearMonth.now())
    val selectionSet = MutableStateFlow(setOf<CalposeDate>())

    MaterialCalendar(monthFlow, selectionSet, dateList, isDark)
}

@ExperimentalCoroutinesApi
@Composable
fun MaterialCalendar(
    monthFlow: MutableStateFlow<YearMonth>,
    selectionSet: MutableStateFlow<Set<CalposeDate>>,
    dateList: MutableList<CalposeDate>,
    isDark: Boolean
) {

    val selections = selectionSet.collectAsState().value

    Column {
        Calpose(
            month = monthFlow.collectAsState().value,

            actions = CalposeActions(
                onClickedPreviousMonth = { monthFlow.value = monthFlow.value.minusMonths(1) },
                onClickedNextMonth = { monthFlow.value = monthFlow.value.plusMonths(1) },
            ),

            widgets = CalposeWidgets(
                header = { month, todayMonth, actions ->
                    MaterialHeader(month, todayMonth, actions, Color(0xFF005661))
                },
                headerDayRow = { headerDayList ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .padding(vertical = 8.dp),
                    ) {
                        headerDayList.forEach {
                            DefaultDay(
                                text = it.name.first().toString(),
                                modifier = Modifier
                                    .weight(WEIGHT_7DAY_WEEK)
                                    .alpha(.6f),
                                style = TextStyle(
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                },
                day = { dayDate, dayList ->
                    val isSelected = selections.contains(dayDate)
                    val onSelected = {
                        selectionSet.value = mutableSetOf(dayList).apply {
                            addAll(dateList)
                        }
                    }
                    val weight = if (isSelected) 1f else WEIGHT_7DAY_WEEK
                    val bgColor = if (isSelected) Color(0xFF00838e) else Color.Transparent

                    val widget: @Composable () -> Unit = {
                        DefaultDay(
                            text = dayDate.day.toString(),
                            modifier = Modifier
                                .padding(4.dp)
                                .weight(weight)
                                .fillMaxWidth(),
                            style = TextStyle(
                                color = when {
                                    isSelected -> Color.White
                                    else -> if(isDark) Color.White else Color.Black
                                },
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }

                    Column(
                        modifier = Modifier.weight(WEIGHT_7DAY_WEEK),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Crossfade(targetState = bgColor) {

                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
//                                        .clickable(onClick = onSelected)
                                    .background(it)

                            ) {
                                widget()
                                onSelected()
                            }
                        }

                    }
                },
                priorMonthDay = { dayDate ->
                    DefaultDay(
                        text = dayDate.day.toString(),
                        style = TextStyle(color = if(isDark) Color.LightGray.copy(alpha = 0.3f) else Color.LightGray),
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth()
                            .weight(WEIGHT_7DAY_WEEK)
                    )
                },
            )
        )
        Divider(
            modifier = Modifier.padding(4.dp),
            color = Color.LightGray,
            thickness = 1.dp
        )
    }
}
