package com.kareanra.habittrack.repository

import com.kareanra.habittrack.model.Habit
import com.kareanra.habittrack.model.dao.HabitDao

class HabitRepository(
    private val dao: HabitDao
) {
    suspend fun create(habit: Habit) =
        dao.create(habit)

    suspend fun update(habit: Habit) =
        dao.update(habit)

    suspend fun delete(id: Long) =
        dao.delete(id)

    suspend fun deleteAll() =
        dao.deleteAll()

    suspend fun load(id: Long): Habit =
        dao.load(id)

    suspend fun loadAll(): List<Habit> =
        dao.loadAll()
}
