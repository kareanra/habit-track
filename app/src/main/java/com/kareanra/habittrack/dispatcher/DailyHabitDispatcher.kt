package com.kareanra.habittrack.dispatcher

import com.kareanra.habittrack.intent.DailyHabitIntent
import com.kareanra.habittrack.model.DailyHabit
import com.kareanra.habittrack.model.dao.DailyHabitDao
import com.kareanra.habittrack.repository.DailyHabitRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Singleton
class DailyHabitDispatcher @Inject constructor(
    dao: DailyHabitDao
) {
    private val repository = DailyHabitRepository(dao)

    fun dispatchIntent(intent: DailyHabitIntent) = flow {
        emit(DailyHabitResult.Loading)
        when (intent) {
            is DailyHabitIntent.LoadById -> {
                val result = loadHabit(intent.id)
                emit(DailyHabitResult.SingleResult(habit = result))
            }
            is DailyHabitIntent.Save -> {
                saveHabit(intent.habit)
                emit(DailyHabitResult.NoResult)
            }
            is DailyHabitIntent.ClearAll -> {
                clearHabits()
                emit(DailyHabitResult.NoResult)
            }
        }
    }.catchingAndRetrying { DailyHabitResult.Failure(it) }

    private suspend fun saveHabit(habit: DailyHabit) = repository.save(habit)

    private suspend fun loadHabit(id: Long): DailyHabit? = repository.findById(id)

    private suspend fun clearHabits() = repository.clearData()
}