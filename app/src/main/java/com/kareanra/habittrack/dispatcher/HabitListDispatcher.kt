package com.kareanra.habittrack.dispatcher

import android.content.SharedPreferences
import com.kareanra.habittrack.intent.HabitListIntent
import com.kareanra.habittrack.model.Habit
import com.kareanra.habittrack.model.dao.HabitDao
import com.kareanra.habittrack.repository.HabitRepository
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitListDispatcher @Inject constructor(
    dao: HabitDao,
    private val sharedPreferences: SharedPreferences
) {
    private val repository = HabitRepository(dao)

    // TODO: check last refresh time in shared prefs?
    fun dispatchIntent(intent: HabitListIntent) =
        flow {
            when (intent) {
                is HabitListIntent.Load -> {
                    emit(HabitListResult.Results(repository.loadAll()))
                }
                is HabitListIntent.NewHabit -> {
                    // TODO: navigate?
                    repository.create(
                        Habit(name = intent.name)
                    )
                    emit(HabitListResult.Results(repository.loadAll()))
                }
                is HabitListIntent.Detail -> {
                    emit(HabitListResult.SingleResult(repository.load(intent.habitId)))
                }
                is HabitListIntent.Clear -> {
                    repository.deleteAll()
                    emit(HabitListResult.NoResult)
                }
            }
        }.onStart { emit(HabitListResult.Loading) }
            .catchingAndRetrying { HabitListResult.Failure(it) }
}
