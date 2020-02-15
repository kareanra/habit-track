package com.kareanra.habittrack.dispatcher

import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.retryWhen
import kotlinx.io.IOException
import mu.KotlinLogging

val logger = KotlinLogging.logger { }

inline fun <R> Flow<R>.catchingAndRetrying(crossinline failureResult: (Throwable) -> R) =
    retryWhen { cause, attempt ->
        if (cause is IOException && attempt <= 3) {
            Log.w("DailyHabitDispatcher", "Hit IOException, retrying... attempt $attempt")
            delay(200)
            true
        } else {
            logger.error(cause) { "Error performing action" }
            false
        }
    }.catch { emit(failureResult(it)) }
