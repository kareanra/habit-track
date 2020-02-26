package com.kareanra.habittrack.view

import com.kareanra.habittrack.model.view.AnswerableHabitView
import com.kareanra.habittrack.model.view.HabitView
import com.kareanra.habittrack.viewmodel.BaseViewModel

data class GraphViewState(
    val loading: Boolean,
    val habits: List<HabitView>,
    val answerableHabits: List<AnswerableHabitView>,
    val error: String?
) : BaseViewModel.State {
    companion object {
        val EMPTY = GraphViewState(
            loading = false,
            habits = emptyList(),
            answerableHabits = emptyList(),
            error = null
        )
    }
}
