package com.kareanra.habittrack.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kareanra.habittrack.HabitTrackApp
import com.kareanra.habittrack.R
import com.kareanra.habittrack.dispatcher.HabitListDispatcher
import com.kareanra.habittrack.dispatcher.HabitListReducer
import com.kareanra.habittrack.intent.HabitListIntent
import com.kareanra.habittrack.model.LongWrapper
import com.kareanra.habittrack.model.view.AnswerableHabitView
import com.kareanra.habittrack.util.showLongToast
import com.kareanra.habittrack.viewmodel.HabitListViewModel
import com.kareanra.habittrack.viewmodel.factory.HabitListViewModelFactory
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class HabitListFragment : Fragment(), CoroutineScope {
    private val job = Job()

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerAdapter: RecyclerAdapter

    @Inject
    lateinit var dispatcher: HabitListDispatcher

    @Inject
    lateinit var reducer: HabitListReducer

    private val viewModel: HabitListViewModel by lazy {
        ViewModelProvider(this, HabitListViewModelFactory(dispatcher, reducer))
            .get(HabitListViewModel::class.java)
    }

    private val habits: MutableList<AnswerableHabitView> = mutableListOf()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context.applicationContext as HabitTrackApp).daggerComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linearLayoutManager = LinearLayoutManager(context)
        recyclerAdapter = RecyclerAdapter(habits)

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = recyclerAdapter

        clear_habits_button.setOnClickListener {
            viewModel.intents.offer(HabitListIntent.Clear)
        }

        new_habit_button.setOnClickListener {
            val directions = HabitListFragmentDirections.listToDetail(LongWrapper(null))
            new_habit_button.findNavController().navigate(directions)
        }

        graph_button.setOnClickListener {
            val intent = Intent(context, GraphViewActivity::class.java)
            startActivity(intent)
        }

        launch {
            viewModel.states.consumeEach(::render)
        }

        launch {
            recyclerAdapter.userUpdates.consumeEach {
                when (it) {
                    is RecyclerViewIntent.HabitSaveClicked -> {
                        viewModel.intents.offer(
                            HabitListIntent.NewAnswer(
                                habitId = it.id,
                                inputType = it.inputType,
                                answer = it.answer,
                                notes = it.notes
                            )
                        )
                    }
                    is RecyclerViewIntent.HabitClicked -> {
                        recyclerAdapter.redraw(RecyclerViewState(it.id))
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.intents.offer(HabitListIntent.Load)
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancel()
        recyclerAdapter.destroy()
    }

    private fun render(viewState: HabitListViewState) {
        when {
            viewState.loading -> Unit
//            viewState.toastText != null ->
//                context.showShortToast(viewState.toastText)
            viewState.error != null ->
                context.showLongToast(viewState.error)
            else -> {
                habits.clear()
                habits.addAll(viewState.habits)
                recyclerAdapter.notifyDataSetChanged()
            }
        }
    }
}