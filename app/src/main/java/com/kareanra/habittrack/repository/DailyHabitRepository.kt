package com.kareanra.habittrack.repository

import com.kareanra.habittrack.model.DailyHabit
import com.kareanra.habittrack.model.dao.DailyHabitDao

class DailyHabitRepository(
    private val dao: DailyHabitDao//,
//    private val api: OxfordDictionaryApi
) {

    suspend fun save(habit: DailyHabit) =
        dao.save(habit)

    suspend fun findById(id: Long): DailyHabit? =
        dao.findById(id)

    suspend fun clearData() =
        dao.deleteAll()
}
