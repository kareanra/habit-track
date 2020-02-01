package com.kareanra.habittrack.viewmodel

import com.kareanra.habittrack.intent.HabitListIntent
import com.kareanra.habittrack.dispatcher.HabitListDispatcher
import com.kareanra.habittrack.dispatcher.HabitListReducer
import com.kareanra.habittrack.dispatcher.HabitListResult
import com.kareanra.habittrack.view.HabitListViewState
import kotlinx.coroutines.flow.Flow

class HabitListViewModel(
    private val dispatcher: HabitListDispatcher,
    reducer: HabitListReducer
) : BaseViewModel<HabitListIntent, HabitListResult, HabitListViewState>(
    HabitListViewState.EMPTY,
    reducer
) {
    override fun handleIntent(intent: HabitListIntent): Flow<HabitListResult> =
        dispatcher.dispatchIntent(intent)
}
