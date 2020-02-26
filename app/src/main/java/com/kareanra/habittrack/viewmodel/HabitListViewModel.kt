package com.kareanra.habittrack.viewmodel

import com.kareanra.habittrack.dispatcher.AnswerDispatcher
import com.kareanra.habittrack.intent.HabitListIntent
import com.kareanra.habittrack.dispatcher.HabitDispatcher
import com.kareanra.habittrack.dispatcher.HabitListReducer
import com.kareanra.habittrack.dispatcher.HabitListResult
import com.kareanra.habittrack.dispatcher.catchingAndRetrying
import com.kareanra.habittrack.model.view.AnswerableHabitView
import com.kareanra.habittrack.model.view.HabitView
import com.kareanra.habittrack.view.HabitListViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.toList
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HabitListViewModel(
    private val answerDispatcher: AnswerDispatcher,
    private val habitDispatcher: HabitDispatcher,
    reducer: HabitListReducer
) : BaseViewModel<HabitListIntent, HabitListResult, HabitListViewState>(
    HabitListViewState.EMPTY,
    reducer
) {
    private val sdf = SimpleDateFormat("yyyyMMdd", Locale.US)

    override fun handleIntent(intent: HabitListIntent): Flow<HabitListResult> {
        val yyyymmdd = sdf.format(Date()).toInt()

        return flow {
            when (intent) {
                is HabitListIntent.Load -> {
                    emit(HabitListResult.Results(
                        loadAllHabitsWithAnswers(yyyymmdd))
                    )
                }
                is HabitListIntent.NewHabit -> {
                    emit(HabitListResult.SingleResult(
                        habitDispatcher.saveHabit(
                            HabitDispatcher.Command.SaveHabit(
                                inputType = intent.inputType,
                                name = intent.name
                            )
                        )
                    ))
                }
                is HabitListIntent.NewAnswer -> {
                    answerDispatcher.saveAnswer(
                        AnswerDispatcher.Command.SaveAnswer(
                            habitId = intent.habitId,
                            inputType = intent.inputType,
                            answer = intent.answer,
                            yyyymmdd = yyyymmdd,
                            notes = intent.notes
                        )
                    )
                    emit(HabitListResult.SaveSuccess)
                    emit(HabitListResult.Results(loadAllHabitsWithAnswers(yyyymmdd)))
                }
                is HabitListIntent.Detail -> {
                    habitDispatcher.loadHabit(intent.habitId)?.let {
                        emit(HabitListResult.SingleResult(it))
                    } ?: emit(HabitListResult.NoResult)
                }
                is HabitListIntent.Clear -> {
                    habitDispatcher.deleteAll()
                    emit(HabitListResult.NoResult)
                }
            }
        }.onStart { emit(HabitListResult.Loading) }
            .catchingAndRetrying { HabitListResult.Failure(it) }
    }

    private suspend fun loadAllHabitsWithAnswers(yyyymmdd: Int): List<AnswerableHabitView> =
        habitDispatcher.loadAll()
            .asFlow()
            .map {
                answerDispatcher.loadAnswer(it.id, yyyymmdd)
                    .map { ans ->
                        AnswerableHabitView(
                            habit = HabitView(
                                id = it.id,
                                inputType = it.inputType,
                                name = it.name
                            ),
                            answer = if (ans.isEmpty()) null else ans
                        )
                    }
            }
            .flattenConcat()
            .toList(ArrayList())
}
