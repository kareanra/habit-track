package com.kareanra.habittrack.intent

import com.kareanra.habittrack.model.InputType
import com.kareanra.habittrack.viewmodel.BaseViewModel

sealed class HabitListIntent : BaseViewModel.Intent {
    object Load : HabitListIntent()
    class NewHabit(val name: String, val inputType: InputType) : HabitListIntent()
    class NewAnswer(val habitId: Long, val inputType: InputType, val answer: Int, val notes: String) : HabitListIntent()
    class Detail(val habitId: Long) : HabitListIntent()
    object Clear : HabitListIntent()
}
