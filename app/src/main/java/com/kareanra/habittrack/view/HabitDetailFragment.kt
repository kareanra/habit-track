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
import com.kareanra.habittrack.dispatcher.HabitListDispatcher
import com.kareanra.habittrack.dispatcher.HabitListReducer
import com.kareanra.habittrack.intent.HabitListIntent
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
    lateinit var dispatcher: HabitListDispatcher

    @Inject
    lateinit var reducer: HabitListReducer

    private val viewModel: HabitListViewModel by lazy {
        ViewModelProvider(this, HabitListViewModelFactory(dispatcher, reducer))
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
        
        habit_detail_name.doAfterTextChanged {
            // TODO: query api for existing habit with that name
            save_habit.isEnabled = !it.isNullOrEmpty()
        }

        save_habit.setOnClickListener {
            if (habit_detail_name.length() > 0) {
                viewModel.intents.offer(HabitListIntent.NewHabit(habit_detail_name.text.toString()))
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // commit changes
    }

    override fun onResume() {
        super.onResume()
        // other stuff
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancel()
    }

    private fun render(viewState: HabitListViewState) {
        when {
            viewState.loading -> {
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
}
