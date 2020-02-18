package com.kareanra.habittrack.dispatcher

import android.content.SharedPreferences
import com.kareanra.habittrack.dispatcher.HabitListResult.Failure
import com.kareanra.habittrack.dispatcher.HabitListResult.Loading
import com.kareanra.habittrack.dispatcher.HabitListResult.NoResult
import com.kareanra.habittrack.dispatcher.HabitListResult.Results
import com.kareanra.habittrack.dispatcher.HabitListResult.SaveSuccess
import com.kareanra.habittrack.dispatcher.HabitListResult.SingleResult
import com.kareanra.habittrack.intent.HabitListIntent
import com.kareanra.habittrack.model.Habit
import com.kareanra.habittrack.model.HabitAnswer
import com.kareanra.habittrack.model.dao.HabitAnswerDao
import com.kareanra.habittrack.model.dao.HabitDao
import com.kareanra.habittrack.model.view.AnswerView
import com.kareanra.habittrack.model.view.AnswerableHabitView
import com.kareanra.habittrack.model.view.HabitView
import com.kareanra.habittrack.repository.HabitAnswerRepository
import com.kareanra.habittrack.repository.HabitRepository
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitListDispatcher @Inject constructor(
    habitDao: HabitDao,
    answerDao: HabitAnswerDao,
    private val sharedPreferences: SharedPreferences
) {
    private val habitRepository = HabitRepository(habitDao)
    private val answerRepository = HabitAnswerRepository(answerDao)

    private val sdf = SimpleDateFormat("yyyyMMdd", Locale.US)

    // TODO: check last refresh time in shared prefs?
    // TODO: break up intents by type (habit vs. answer)
    fun dispatchIntent(intent: HabitListIntent): Flow<HabitListResult> {
        val yyyymmdd = sdf.format(Date()).toInt()

        return flow {
            when (intent) {
                is HabitListIntent.Load -> {
                    emit(Results(loadAll(yyyymmdd)))
                }
                is HabitListIntent.NewHabit -> {
                    val newHabitId = habitRepository.create(
                        Habit(
                            inputType = intent.inputType,
                            name = intent.name
                        )
                    )
                    emit(SingleResult(habitRepository.load(newHabitId)))
                }
                is HabitListIntent.NewAnswer -> {
                    answerRepository.save(
                        HabitAnswer(
                            inputType = intent.inputType,
                            answer = intent.answer,
                            yyyymmdd = yyyymmdd,
                            notes = intent.notes,
                            habitId = intent.habitId
                        )
                    )
                    emit(SaveSuccess)
                    emit(Results(loadAll(yyyymmdd)))
                }
                is HabitListIntent.Detail -> {
                    emit(SingleResult(habitRepository.load(intent.habitId)))
                }
                is HabitListIntent.Clear -> {
                    habitRepository.deleteAll()
                    emit(NoResult)
                }
            }
        }.onStart { emit(Loading) }
            .catchingAndRetrying { Failure(it) }
    }

    private suspend fun loadAll(yyyymmdd: Int): List<AnswerableHabitView> =
        habitRepository.loadAll()
            .asFlow()
            .map {
                loadAnswer(it.id, yyyymmdd)
                    .map { ans ->
                        AnswerableHabitView(
                            habit = HabitView(
                                id = it.id,
                                name = it.name
                            ),
                            inputType = it.inputType,
                            answer = if (ans.isEmpty()) null else ans
                        )
                    }
            }
            .flattenConcat()
            .toList(ArrayList())

    private fun loadAnswer(habitId: Long, yyyymmdd: Int): Flow<AnswerView> =
        flow {
            answerRepository.findByHabitAndDay(habitId, yyyymmdd)?.let {
                emit(
                    AnswerView(
                        yyyymmdd = it.yyyymmdd,
                        value = it.answer,
                        notes = it.notes ?: ""
                    )
                )
            } ?: emit(AnswerView.empty())
        }
}
