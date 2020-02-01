package com.kareanra.habittrack.dispatcher

import com.kareanra.habittrack.viewmodel.BaseViewModel

interface Reducer<S: BaseViewModel.State, R: BaseViewModel.Result> {

    fun reduce(existingState: S, result: R): S
}
