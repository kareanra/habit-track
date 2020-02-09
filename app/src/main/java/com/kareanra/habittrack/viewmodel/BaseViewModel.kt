package com.kareanra.habittrack.viewmodel

import androidx.lifecycle.ViewModel
import com.kareanra.habittrack.dispatcher.Reducer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

abstract class BaseViewModel<I : BaseViewModel.Intent, R : BaseViewModel.Result, S : BaseViewModel.State>(
    initial: S,
    reducer: Reducer<S, R>
) : ViewModel(), CoroutineScope {

    override val coroutineContext
        get() = SupervisorJob() + IO

    interface Intent
    interface Result
    interface State

    private val _intents = Channel<I>(Channel.CONFLATED)
    val intents: SendChannel<I> = _intents

    private val resultFlows = Channel<Flow<R>>(Channel.UNLIMITED)

    private val _states = ConflatedBroadcastChannel(initial)
    val states: ReceiveChannel<S>
        get() = _states.openSubscription()

    private val currentState
        get() = _states.value

    init {
        // bind intents to results
        launch {
            _intents.consumeEach { intent ->
                resultFlows.send(handleIntent(intent))
            }
        }
        // bind results to new states
        launch {
            resultFlows.consumeAsFlow()
                .flattenConcat()
                .map { reducer.reduce(currentState, it) }
                .distinctUntilChanged()
                .collect {
                    _states.send(it)
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineContext.cancel()
    }

    protected abstract fun handleIntent(intent: I): Flow<R>
}