package com.kareanra.habittrack.view

import com.kareanra.habittrack.model.Habit
import com.kareanra.habittrack.viewmodel.BaseViewModel

data class HabitListViewState(
    val loading: Boolean,
    val habits: List<Habit>,
    val error: String?
) : BaseViewModel.State {
    companion object {
        val EMPTY = HabitListViewState(
            loading = false,
            habits = emptyList(),
            error = null
        )
    }
}
