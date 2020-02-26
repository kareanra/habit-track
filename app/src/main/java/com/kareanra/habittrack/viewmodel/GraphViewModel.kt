package com.kareanra.habittrack.viewmodel

import com.kareanra.habittrack.dispatcher.AnswerDispatcher
import com.kareanra.habittrack.dispatcher.GraphViewReducer
import com.kareanra.habittrack.dispatcher.GraphViewResult
import com.kareanra.habittrack.dispatcher.HabitDispatcher
import com.kareanra.habittrack.dispatcher.catchingAndRetrying
import com.kareanra.habittrack.intent.GraphViewIntent
import com.kareanra.habittrack.model.InputType
import com.kareanra.habittrack.model.view.AnswerView
import com.kareanra.habittrack.model.view.AnswerableHabitView
import com.kareanra.habittrack.view.GraphViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

class GraphViewModel(
    private val answerDispatcher: AnswerDispatcher,
    private val habitDispatcher: HabitDispatcher,
    reducer: GraphViewReducer
) : BaseViewModel<GraphViewIntent, GraphViewResult, GraphViewState>(
    GraphViewState.EMPTY,
    reducer
) {
    override fun handleIntent(intent: GraphViewIntent): Flow<GraphViewResult> = flow {
        when (intent) {
            is GraphViewIntent.LoadAllHabits ->
                emit(GraphViewResult.HabitResults(habitDispatcher.loadHabitsOfType(InputType.RANGE)))
            is GraphViewIntent.LoadAnswersByDay ->
                emit(GraphViewResult.AnswerableHabitResults(
                    loadHabitsAndAnswersByDay(yyyymmdds = emptyList()) // TODO
                ))
        }
    }.onStart {
        emit(GraphViewResult.Loading)
    }.catchingAndRetrying { GraphViewResult.Failure(it) }

    private suspend fun loadHabitsAndAnswersByDay(yyyymmdds: List<Int>) =
        yyyymmdds.flatMap {
            answerDispatcher.loadAllByDay(it)
        }.map {
            AnswerableHabitView(
                habit = habitDispatcher.loadHabit(it.habitId)!!,
                answer = AnswerView(
                    yyyymmdd = it.yyyymmdd,
                    value = it.answer,
                    notes = it.notes ?: ""
                )
            )
        }
}