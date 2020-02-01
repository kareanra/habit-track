package com.kareanra.habittrack.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kareanra.habittrack.model.Habit

@Dao
interface HabitDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun create(habit: Habit): Long

    @Update
    suspend fun update(habit: Habit)

    @Query("DELETE FROM `Habit` WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM `Habit`")
    suspend fun deleteAll()

    @Query("SELECT * FROM `Habit` ORDER BY ordinal")
    suspend fun loadAll(): List<Habit>
}
