package com.kareanra.habittrack.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.kareanra.habittrack.HabitTrackApp
import com.kareanra.habittrack.R
import com.kareanra.habittrack.dispatcher.HabitListDispatcher
import com.kareanra.habittrack.dispatcher.HabitListReducer
import com.kareanra.habittrack.intent.HabitListIntent
import com.kareanra.habittrack.model.Habit
import com.kareanra.habittrack.viewmodel.HabitListViewModel
import com.kareanra.habittrack.viewmodel.factory.HabitListViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class HomeActivity : AppCompatActivity(), CoroutineScope, LifecycleOwner {

    private val job = Job()

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerAdapter: RecyclerAdapter

    @Inject
    lateinit var dispatcher: HabitListDispatcher

    @Inject
    lateinit var reducer: HabitListReducer

    private val viewModel: HabitListViewModel by lazy {
        ViewModelProvider(this, HabitListViewModelFactory(dispatcher, reducer)).get(HabitListViewModel::class.java)
    }

    private val habits: MutableList<Habit> = mutableListOf()

    override val coroutineContext: CoroutineContext
        get() = job + Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        (application as HabitTrackApp).daggerComponent.inject(this)

        linearLayoutManager = LinearLayoutManager(this)
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
            viewModel.intents.offer(HabitListIntent.NewHabit(textField.text.toString()))
        }

        launch {
            viewModel.states.consumeEach(::render)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancel()
    }

    private fun render(viewState: HabitListViewState) {
        when {
            viewState.loading ->
                textField.text = getString(R.string.loading)
            viewState.error != null ->
                Toast.makeText(this, viewState.error, Toast.LENGTH_LONG).show()
            else -> {
                textField.text = "My Habit"

                habits.clear()
                habits.addAll(viewState.habits)
                recyclerAdapter.notifyDataSetChanged()
            }
        }
    }
}
