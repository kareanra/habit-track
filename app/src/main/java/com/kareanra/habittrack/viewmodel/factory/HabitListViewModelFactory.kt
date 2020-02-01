package com.kareanra.habittrack.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kareanra.habittrack.dispatcher.HabitListDispatcher
import com.kareanra.habittrack.dispatcher.HabitListReducer
import com.kareanra.habittrack.viewmodel.HabitListViewModel

class HabitListViewModelFactory(
    private val dispatcher: HabitListDispatcher,
    private val reducer: HabitListReducer
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HabitListViewModel(dispatcher, reducer) as T
    }
}
