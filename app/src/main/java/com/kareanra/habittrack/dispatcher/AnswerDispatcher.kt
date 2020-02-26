package com.kareanra.habittrack.dispatcher

import android.content.SharedPreferences
import com.kareanra.habittrack.model.HabitAnswer
import com.kareanra.habittrack.model.InputType
import com.kareanra.habittrack.model.dao.HabitAnswerDao
import com.kareanra.habittrack.model.view.AnswerView
import com.kareanra.habittrack.repository.HabitAnswerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnswerDispatcher @Inject constructor(
    answerDao: HabitAnswerDao,
    private val sharedPreferences: SharedPreferences
) {
    private val answerRepository = HabitAnswerRepository(answerDao)

    fun loadAnswer(habitId: Long, yyyymmdd: Int): Flow<AnswerView> =
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

    suspend fun loadAllByDay(yyyymmdd: Int) =
        answerRepository.findAllByDay(yyyymmdd)

    suspend fun saveAnswer(command: Command.SaveAnswer) {
        answerRepository.save(
            HabitAnswer(
                inputType = command.inputType,
                answer = command.answer,
                yyyymmdd = command.yyyymmdd,
                notes = command.notes,
                habitId = command.habitId
            )
        )
    }

    suspend fun deleteAllByDay(yyyymmdd: Int) =
        answerRepository.deleteByDay(yyyymmdd)

    sealed class Command {
        data class SaveAnswer(
            val habitId: Long,
            val inputType: InputType,
            val answer: Int,
            val yyyymmdd: Int,
            val notes: String
        ) : Command()
    }
}
