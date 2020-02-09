package com.kareanra.habittrack.intent

import com.kareanra.habittrack.viewmodel.BaseViewModel

sealed class HabitListIntent : BaseViewModel.Intent {
    object Load : HabitListIntent()
    class NewHabit(val name: String) : HabitListIntent()
    class Detail(val habitId: Long) : HabitListIntent()
    object Clear : HabitListIntent()
}
