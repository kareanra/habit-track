package com.kareanra.habittrack.dispatcher

import com.kareanra.habittrack.model.view.AnswerableHabitView
import com.kareanra.habittrack.model.view.HabitView
import com.kareanra.habittrack.viewmodel.BaseViewModel

sealed class GraphViewResult : BaseViewModel.Result {
    object Loading : GraphViewResult()
    object NoResult : GraphViewResult()
    class HabitResults(val habits: List<HabitView>) : GraphViewResult()
    class AnswerableHabitResults(val answerableHabits: List<AnswerableHabitView>) : GraphViewResult()
    class Failure(val throwable: Throwable) : GraphViewResult()
}
