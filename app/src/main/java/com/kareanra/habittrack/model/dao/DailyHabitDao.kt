package com.kareanra.habittrack.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kareanra.habittrack.model.DailyHabit

@Dao
interface DailyHabitDao {

    @Query("SELECT * FROM `DailyHabit` WHERE id = :id LIMIT 1") // TODO: need limit?
    suspend fun findById(id: Long): DailyHabit?

    @Query("SELECT * FROM `DailyHabit` WHERE day_id = :dayId")
    suspend fun findAllByDay(dayId: Long): List<DailyHabit>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun save(dailyHabit: DailyHabit): Long

    @Update
    suspend fun update(dailyHabit: DailyHabit)

    @Query("DELETE FROM `DailyHabit`")
    suspend fun deleteAll()
}