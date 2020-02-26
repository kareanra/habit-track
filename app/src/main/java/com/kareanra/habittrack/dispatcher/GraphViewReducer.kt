package com.kareanra.habittrack.dispatcher

import com.kareanra.habittrack.view.GraphViewState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GraphViewReducer @Inject constructor() : Reducer<GraphViewState, GraphViewResult> {

    override fun reduce(existingState: GraphViewState, result: GraphViewResult) =
        when (result) {
            is GraphViewResult.Loading ->
                existingState.copy(
                    loading = true
                )
            is GraphViewResult.NoResult ->
                existingState.copy(
                    loading = false
                )
            is GraphViewResult.HabitResults ->
                existingState.copy(
                    loading = false,
                    habits = result.habits
                )
            is GraphViewResult.AnswerableHabitResults ->
                existingState.copy(
                    loading = false,
                    answerableHabits = result.answerableHabits
                )
            is GraphViewResult.Failure ->
                existingState.copy(
                    loading = false,
                    habits = emptyList(),
                    error = result.throwable.message
                )
        }
}
