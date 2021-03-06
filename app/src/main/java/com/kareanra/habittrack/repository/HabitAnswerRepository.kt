package com.kareanra.habittrack.repository

import com.kareanra.habittrack.model.HabitAnswer
import com.kareanra.habittrack.model.dao.HabitAnswerDao

class HabitAnswerRepository(
    private val dao: HabitAnswerDao
) {
    suspend fun save(answer: HabitAnswer) =
        dao.save(answer)

    suspend fun findAllByDay(yyyymmdd: Int) =
        dao.findAllByDay(yyyymmdd)

    suspend fun findByHabitAndDay(habitId: Long, yyyymmdd: Int) =
        dao.findByHabitAndYyyyMmDd(habitId, yyyymmdd)

    suspend fun findById(id: Long): HabitAnswer? =
        dao.findById(id)

    suspend fun deleteByDay(yyyymmdd: Int) =
        dao.deleteByDay(yyyymmdd)
}
