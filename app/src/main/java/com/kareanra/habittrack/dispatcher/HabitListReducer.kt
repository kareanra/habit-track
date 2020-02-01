package com.kareanra.habittrack.dispatcher

import com.kareanra.habittrack.view.HabitListViewState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitListReducer @Inject constructor() : Reducer<HabitListViewState, HabitListResult> {

    override fun reduce(existingState: HabitListViewState, result: HabitListResult) = when (result) {
        is HabitListResult.Loading -> existingState.copy(
            loading = true,
            habits = emptyList()
        )
        is HabitListResult.Results -> existingState.copy(
            loading = false,
            habits = result.habits
        )
        is HabitListResult.Failure -> existingState.copy(
            loading = false,
            habits = emptyList(),
            error = result.throwable.message
        )
        is HabitListResult.NoResult -> existingState
    }
}