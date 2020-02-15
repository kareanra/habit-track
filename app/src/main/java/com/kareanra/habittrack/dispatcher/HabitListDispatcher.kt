package com.kareanra.habittrack.dispatcher

import android.content.SharedPreferences
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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
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
    fun dispatchIntent(intent: HabitListIntent): Flow<HabitListResult> {
        val yyyymmdd = sdf.format(Date()).toInt()

        return flow {
            when (intent) {
                is HabitListIntent.Load -> {
                    emit(HabitListResult.Results(loadAll(yyyymmdd)))
                }
                is HabitListIntent.NewHabit -> {
                    habitRepository.create(
                        Habit(name = intent.name)
                    )
                    emit(HabitListResult.Results(loadAll(yyyymmdd)))
                }
                is HabitListIntent.NewAnswer -> {
                    answerRepository.save(
                        HabitAnswer(
                            answer = intent.answer,
                            yyyymmdd = yyyymmdd,
                            notes = intent.notes,
                            habitId = intent.habitId
                        )
                    )
                    emit(HabitListResult.SaveSuccess)
                    emit(HabitListResult.Results(loadAll(yyyymmdd)))
                }
                is HabitListIntent.Detail -> {
                    emit(HabitListResult.SingleResult(habitRepository.load(intent.habitId)))
                }
                is HabitListIntent.Clear -> {
                    habitRepository.deleteAll()
                    emit(HabitListResult.NoResult)
                }
            }
        }.onStart { emit(HabitListResult.Loading) }
            .catchingAndRetrying { HabitListResult.Failure(it) }
    }

    private suspend fun loadAll(today: Int) =
        habitRepository.loadAll()
            .map {
                AnswerableHabitView(
                    habit = HabitView(
                        id = it.id,
                        name = it.name
                    ),
                    answer = answerRepository.findByHabitAndDay(it.id, today)?.let { ans ->
                        AnswerView(
                            yyyymmdd = ans.yyyymmdd,
                            value = ans.answer,
                            notes = ans.notes ?: ""
                        )
                    }
                )
            }
}
