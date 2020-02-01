package com.kareanra.habittrack.model.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kareanra.habittrack.model.DailyHabit
import com.kareanra.habittrack.model.Day
import com.kareanra.habittrack.model.Habit
import com.kareanra.habittrack.model.dao.DailyHabitDao
import com.kareanra.habittrack.model.dao.HabitDao

@Database(entities = [Habit::class, Day::class, DailyHabit::class], version = 1, exportSchema = false)
abstract class HabitDatabase : RoomDatabase() {

    abstract fun dailyHabitDao(): DailyHabitDao

    abstract fun habbitDao(): HabitDao
}