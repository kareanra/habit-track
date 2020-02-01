package com.kareanra.habittrack.intent

import com.kareanra.habittrack.model.DailyHabit
import com.kareanra.habittrack.model.Day
import com.kareanra.habittrack.model.Habit
import com.kareanra.habittrack.viewmodel.BaseViewModel

sealed class DailyHabitIntent : BaseViewModel.Intent {
    object LoadAll : DailyHabitIntent()
    object ClearAll : DailyHabitIntent()
    class Save(val habit: DailyHabit) : DailyHabitIntent()
    class LoadById(val id: Long) : DailyHabitIntent()
    class LoadByDay(val day: Day) : DailyHabitIntent()
    class LoadByHabit(val habit: Habit) : DailyHabitIntent()
}
