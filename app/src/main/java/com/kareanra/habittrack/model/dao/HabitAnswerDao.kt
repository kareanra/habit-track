package com.kareanra.habittrack.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kareanra.habittrack.model.HabitAnswer

@Dao
interface HabitAnswerDao {

    @Query("SELECT * FROM HabitAnswer WHERE id = :id LIMIT 1") // TODO: need limit?
    suspend fun findById(id: Long): HabitAnswer?

    @Query("SELECT * FROM HabitAnswer WHERE habit_id = :habitId and yyyymmdd = :yyyymmdd LIMIT 1")
    suspend fun findByHabitAndYyyyMmDd(habitId: Long, yyyymmdd: Int): HabitAnswer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(habitAnswer: HabitAnswer): Long

    @Update
    suspend fun update(habitAnswer: HabitAnswer)

    @Query("DELETE FROM HabitAnswer")
    suspend fun deleteAll()
}
