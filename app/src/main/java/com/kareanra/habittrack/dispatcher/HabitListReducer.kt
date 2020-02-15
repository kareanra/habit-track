package com.kareanra.habittrack.dispatcher

import com.kareanra.habittrack.view.HabitListViewState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitListReducer @Inject constructor() : Reducer<HabitListViewState, HabitListResult> {

    override fun reduce(existingState: HabitListViewState, result: HabitListResult) =
        when (result) {
            is HabitListResult.Loading ->
                existingState.copy(
                    loading = true,
                    habits = existingState.habits
                )
            is HabitListResult.Results ->
                existingState.copy(
                    loading = false,
                    habits = result.habits
                )
            is HabitListResult.SaveSuccess ->
                existingState.copy(
                    loading = false,
                    toastText = "Answer successfully saved"
                )
            is HabitListResult.SingleResult ->
                existingState.copy(
                    loading = false,
                    detailHabit = result.habit,
                    habits = emptyList()
                )
            is HabitListResult.Failure ->
                existingState.copy(
                    loading = false,
                    habits = emptyList(),
                    error = result.throwable.message
                )
            is HabitListResult.NoResult ->
                HabitListViewState.EMPTY
        }
}
