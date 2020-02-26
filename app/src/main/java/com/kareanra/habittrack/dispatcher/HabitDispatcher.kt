package com.kareanra.habittrack.dispatcher

import android.content.SharedPreferences
import com.kareanra.habittrack.model.Habit
import com.kareanra.habittrack.model.InputType
import com.kareanra.habittrack.model.dao.HabitAnswerDao
import com.kareanra.habittrack.model.dao.HabitDao
import com.kareanra.habittrack.model.view.HabitView
import com.kareanra.habittrack.repository.HabitAnswerRepository
import com.kareanra.habittrack.repository.HabitRepository
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitDispatcher @Inject constructor(
    habitDao: HabitDao,
    answerDao: HabitAnswerDao,
    private val sharedPreferences: SharedPreferences
) {
    private val habitRepository = HabitRepository(habitDao)
    private val answerRepository = HabitAnswerRepository(answerDao)

    suspend fun loadHabit(habitId: Long): HabitView? =
        habitRepository.load(habitId)?.let {
            HabitView(
                id = habitId,
                inputType = it.inputType,
                name = it.name
            )
        }

    suspend fun loadAll(): List<HabitView> =
        habitRepository.loadAll()
            .map {
                HabitView(
                    id = it.id,
                    inputType = it.inputType,
                    name = it.name
                )
            }

    suspend fun loadHabitsOfType(inputType: InputType): List<HabitView> =
        habitRepository.loadAll()
            .asFlow()
            .filter {
                it.inputType == InputType.RANGE
            }
            .map {
                HabitView(
                    id = it.id,
                    inputType = it.inputType,
                    name = it.name
                )
            }
            .toList(ArrayList())

    suspend fun saveHabit(command: Command.SaveHabit): HabitView {
        val id = habitRepository.create(
            Habit(
                inputType = command.inputType,
                name = command.name
            )
        )
        return HabitView(
            id = id,
            inputType = command.inputType,
            name = command.name
        )
    }

    suspend fun deleteAll() =
        habitRepository.deleteAll()

    sealed class Command {
        data class SaveHabit(
            val inputType: InputType,
            val name: String
        ) : Command()
    }
}
