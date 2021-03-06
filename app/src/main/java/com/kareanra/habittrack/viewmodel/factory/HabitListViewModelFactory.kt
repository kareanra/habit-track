package com.kareanra.habittrack.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kareanra.habittrack.dispatcher.AnswerDispatcher
import com.kareanra.habittrack.dispatcher.HabitDispatcher
import com.kareanra.habittrack.dispatcher.HabitListReducer
import com.kareanra.habittrack.viewmodel.HabitListViewModel

class HabitListViewModelFactory(
    private val answerDispatcher: AnswerDispatcher,
    private val dispatcher: HabitDispatcher,
    private val reducer: HabitListReducer
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HabitListViewModel(answerDispatcher, dispatcher, reducer) as T
    }
}
