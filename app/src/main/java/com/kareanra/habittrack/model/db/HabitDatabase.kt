package com.kareanra.habittrack.model.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kareanra.habittrack.model.HabitAnswer
import com.kareanra.habittrack.model.Day
import com.kareanra.habittrack.model.Habit
import com.kareanra.habittrack.model.dao.HabitAnswerDao
import com.kareanra.habittrack.model.dao.HabitDao

@Database(entities = [Habit::class, Day::class, HabitAnswer::class], version = 1, exportSchema = false)
@TypeConverters(RoomConverters::class)
abstract class HabitDatabase : RoomDatabase() {

    abstract fun dailyHabitDao(): HabitAnswerDao

    abstract fun habitDao(): HabitDao
}
