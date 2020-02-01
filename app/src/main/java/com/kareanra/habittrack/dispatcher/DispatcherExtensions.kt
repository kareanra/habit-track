package com.kareanra.habittrack.dispatcher

import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.retryWhen
import kotlinx.io.IOException

inline fun <R> Flow<R>.catchingAndRetrying(crossinline failureResult: (Throwable) -> R) =
    retryWhen { cause, attempt ->
        if (cause is IOException && attempt <= 3) {
            Log.w("DailyHabitDispatcher", "Hit IOException, retrying... attempt $attempt")
            delay(500)
            true
        } else {
            emit(failureResult(cause))
            false
        }
    }.catch { emit(failureResult(it)) }
