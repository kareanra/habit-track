package com.kareanra.habittrack.view

import android.content.Context
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
import com.kareanra.habittrack.model.Habit
import com.kareanra.habittrack.model.LongWrapper
import com.kareanra.habittrack.util.showLongToast
import com.kareanra.habittrack.util.showShortToast
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

    private val habits: MutableList<Habit> = mutableListOf()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context.applicationContext as HabitTrackApp).daggerComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadHabitsButton.setOnClickListener {
            viewModel.intents.offer(HabitListIntent.Load)
        }

        linearLayoutManager = LinearLayoutManager(context)
        recyclerAdapter = RecyclerAdapter(habits)

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = recyclerAdapter

        loadHabitsButton.setOnClickListener {
            viewModel.intents.offer(HabitListIntent.Load)
        }

        clearHabitsButton.setOnClickListener {
            viewModel.intents.offer(HabitListIntent.Clear)
        }

        newHabitButton.setOnClickListener {
            val directions = HabitListFragmentDirections.listToDetail(LongWrapper(null))
            newHabitButton.findNavController().navigate(directions)
        }

        launch {
            viewModel.states.consumeEach(::render)
        }

        launch {
            recyclerAdapter.userUpdates.consumeEach {
                when (it) {
                    is RecyclerViewIntent.HabitEditClicked -> {
                        val directions = HabitListFragmentDirections.listToDetail(LongWrapper(it.id))
                        loadHabitsButton.findNavController().navigate(directions)
                    }
                    is RecyclerViewIntent.HabitClicked -> {
                        // slide down
                        context.showShortToast("Clicked habit!")
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancel()
        recyclerAdapter.destroy()
    }

    private fun render(viewState: HabitListViewState) {
        when {
            viewState.loading ->
                context.showShortToast(getString(R.string.loading))
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