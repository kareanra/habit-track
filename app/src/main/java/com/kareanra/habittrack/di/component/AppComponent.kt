package com.kareanra.habittrack.di.component

import com.kareanra.habittrack.view.HomeActivity
import com.kareanra.habittrack.di.modules.ApiModule
import com.kareanra.habittrack.di.modules.AppModule
import com.kareanra.habittrack.di.modules.DbModule
import com.kareanra.habittrack.view.HabitListDetailActivity
import com.kareanra.habittrack.view.HabitListDetailFragment
import dagger.Component
import javax.inject.Singleton

@Component(modules = [
    AppModule::class,
    ApiModule::class,
    DbModule::class
])
@Singleton
interface AppComponent {

    fun inject(target: HomeActivity)

    fun inject(target: HabitListDetailActivity)

    fun inject(target: HabitListDetailFragment)
}
