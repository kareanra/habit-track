package com.kareanra.habittrack.intent

import com.kareanra.habittrack.viewmodel.BaseViewModel

sealed class GraphViewIntent : BaseViewModel.Intent {
    object LoadAllHabits : GraphViewIntent()
    class LoadAnswersByDay(val yyyymmdd: Int) : GraphViewIntent()
}
