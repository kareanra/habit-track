package com.kareanra.habittrack.di.modules

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val app: Application) {

    @Provides
    @Singleton
    fun provideContext(): Context =
        app

    @Provides
    @Singleton
    fun provideSharedPreferences(): SharedPreferences =
        app.getSharedPreferences("shared-prefs", MODE_PRIVATE)
}
