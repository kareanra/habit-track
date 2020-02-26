package com.kareanra.habittrack.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kareanra.habittrack.HabitTrackApp
import com.kareanra.habittrack.R
import com.kareanra.habittrack.dispatcher.AnswerDispatcher
import com.kareanra.habittrack.dispatcher.HabitDispatcher
import com.kareanra.habittrack.dispatcher.HabitListReducer
import com.kareanra.habittrack.intent.HabitListIntent
import com.kareanra.habittrack.model.InputType
import com.kareanra.habittrack.util.showLongToast
import com.kareanra.habittrack.viewmodel.HabitListViewModel
import com.kareanra.habittrack.viewmodel.factory.HabitListViewModelFactory
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class HabitDetailFragment : Fragment(), CoroutineScope {

    @Inject
    lateinit var answerDispatcher: AnswerDispatcher

    @Inject
    lateinit var habitDispatcher: HabitDispatcher

    @Inject
    lateinit var reducer: HabitListReducer

    private val viewModel: HabitListViewModel by lazy {
        ViewModelProvider(this, HabitListViewModelFactory(answerDispatcher, habitDispatcher, reducer))
            .get(HabitListViewModel::class.java)
    }

    override val coroutineContext: CoroutineContext
        get() = Job() + IO

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context.applicationContext as HabitTrackApp).daggerComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        launch(Main) {
            viewModel.states.consumeEach(::render)
        }

        // initialize by loading existing habit if id present
        arguments?.let {
            HabitDetailFragmentArgs.fromBundle(it).habitId.value
        }?.let {
            viewModel.intents.offer(HabitListIntent.Detail(it))
        }

        bindViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancel()
    }

    private fun bindViews() {
        habit_detail_name.doAfterTextChanged {
            // TODO: query api for existing habit with that name and post an intent and re-render
            save_habit.isEnabled = isInputValid()
        }

        detail_radio_group.setOnCheckedChangeListener { _, _ ->
            save_habit.isEnabled = isInputValid()
        }

        save_habit.setOnClickListener {
            if (isInputValid()) {
                viewModel.intents.offer(
                    HabitListIntent.NewHabit(
                        name = habit_detail_name.text.toString(),
                        inputType = selectedInputType()!!
                    )
                )
            }
        }
    }

    private fun render(viewState: HabitListViewState) {
        when {
            viewState.toastText != null -> {
                save_habit.isEnabled = false
                habit_detail_name.isEnabled = false
                habit_detail_name.setText(R.string.loading)
            }
            viewState.error != null -> {
                context.showLongToast(viewState.error)
            }
            viewState.detailHabit != null -> {
                save_habit.isEnabled = false
                habit_detail_name.setText(viewState.detailHabit.name)
                habit_detail_name.isEnabled = false
            }
            else -> {
                habit_detail_name.isEnabled = true
                habit_detail_name.requestFocus()
            }
        }
    }

    private fun isInputValid() =
        !habit_detail_name.text.isNullOrEmpty() && detail_radio_group.checkedRadioButtonId != -1

    private fun selectedInputType() =
        when {
            detail_radio_range.isChecked -> InputType.RANGE
            detail_radio_yes_no.isChecked -> InputType.YES_NO
            detail_radio_numerical.isChecked -> InputType.NUMERICAL
            else -> null
        }
}
