package com.kareanra.habittrack.di.modules

import android.content.Context
import androidx.room.Room
import com.kareanra.habittrack.model.dao.HabitAnswerDao
import com.kareanra.habittrack.model.dao.HabitDao
import com.kareanra.habittrack.model.db.HabitDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DbModule {

    @Provides
    @Singleton
    fun provideHabitDatabase(context: Context): HabitDatabase =
        Room.databaseBuilder(context, HabitDatabase::class.java, "HabitDatabase.db")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()

    @Provides
    @Singleton
    fun provideDailyHabitDao(database: HabitDatabase): HabitAnswerDao =
        database.dailyHabitDao()

    @Provides
    @Singleton
    fun provideHabitDao(database: HabitDatabase): HabitDao =
        database.habitDao()
}
