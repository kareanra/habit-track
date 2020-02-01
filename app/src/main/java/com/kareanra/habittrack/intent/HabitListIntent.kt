package com.kareanra.habittrack.intent

import com.kareanra.habittrack.model.Habit
import com.kareanra.habittrack.viewmodel.BaseViewModel

sealed class HabitListIntent : BaseViewModel.Intent {
    object Load : HabitListIntent()
    class NewHabit(val name: String) : HabitListIntent()
    class Detail(val habit: Habit) : HabitListIntent()
    object Clear : HabitListIntent()
}
