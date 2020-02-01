package com.kareanra.habittrack.dispatcher

import com.kareanra.habittrack.model.DailyHabit

sealed class DailyHabitResult {
    object Loading : DailyHabitResult()
    object NoResult : DailyHabitResult()
    class Results(val habits: List<DailyHabit>) : DailyHabitResult()
    class SingleResult(val habit: DailyHabit?) : DailyHabitResult()
    class Failure(val throwable: Throwable) : DailyHabitResult()
}