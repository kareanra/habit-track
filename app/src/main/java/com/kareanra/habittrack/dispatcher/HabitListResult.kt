package com.kareanra.habittrack.dispatcher

import com.kareanra.habittrack.model.Habit
import com.kareanra.habittrack.model.view.AnswerableHabitView
import com.kareanra.habittrack.viewmodel.BaseViewModel

sealed class HabitListResult : BaseViewModel.Result {
    object Loading : HabitListResult()
    object NoResult : HabitListResult()
    object SaveSuccess : HabitListResult()
    class Results(val habits: List<AnswerableHabitView>) : HabitListResult()
    class SingleResult(val habit: Habit) : HabitListResult()
    class Failure(val throwable: Throwable) : HabitListResult()
}
