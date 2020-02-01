package com.kareanra.habittrack

import android.app.Application
import com.kareanra.habittrack.di.component.AppComponent
import com.kareanra.habittrack.di.component.DaggerAppComponent
import com.kareanra.habittrack.di.modules.AppModule

class HabitTrackApp : Application() {

    lateinit var daggerComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        daggerComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }
}
