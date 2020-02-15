package com.kareanra.habittrack.view

import com.kareanra.habittrack.model.Habit
import com.kareanra.habittrack.model.view.AnswerableHabitView
import com.kareanra.habittrack.viewmodel.BaseViewModel

data class HabitListViewState(
    val loading: Boolean,
    val toastText: String?,
    val detailHabit: Habit?,
    val habits: List<AnswerableHabitView>,
    val error: String?
) : BaseViewModel.State {
    companion object {
        val EMPTY = HabitListViewState(
            loading = false,
            toastText = null,
            detailHabit = null,
            habits = emptyList(),
            error = null
        )
    }
}
