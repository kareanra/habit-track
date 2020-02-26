package com.kareanra.habittrack.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kareanra.habittrack.dispatcher.AnswerDispatcher
import com.kareanra.habittrack.dispatcher.GraphViewReducer
import com.kareanra.habittrack.dispatcher.HabitDispatcher
import com.kareanra.habittrack.viewmodel.GraphViewModel

class GraphViewModelFactory(
    private val answerDispatcher: AnswerDispatcher,
    private val dispatcher: HabitDispatcher,
    private val reducer: GraphViewReducer
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GraphViewModel(answerDispatcher, dispatcher, reducer) as T
    }
}
