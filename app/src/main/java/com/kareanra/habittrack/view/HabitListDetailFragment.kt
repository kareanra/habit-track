package com.kareanra.habittrack.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kareanra.habittrack.HabitTrackApp
import com.kareanra.habittrack.R
import com.kareanra.habittrack.intent.HabitListIntent
import com.kareanra.habittrack.model.Habit
import com.kareanra.habittrack.dispatcher.HabitListDispatcher
import com.kareanra.habittrack.dispatcher.HabitListReducer
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

class HabitListDetailFragment : Fragment(), CoroutineScope, LifecycleOwner {

    @Inject
    lateinit var dispatcher: HabitListDispatcher

    @Inject
    lateinit var reducer: HabitListReducer

    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    private val habits = mutableListOf(Habit(0, "Demo"))

    private val viewModel: HabitListViewModel by lazy {
        ViewModelProvider(this, HabitListViewModelFactory(dispatcher, reducer)).get(HabitListViewModel::class.java)
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

        // set up views
        layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recyclerAdapter = RecyclerAdapter(habits)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = recyclerAdapter

        launch(IO) {
            recyclerAdapter.userUpdates.consumeEach {
                Log.d("HabitListDetailFragment", "Clicked on habit $it")
            }
        }

        view.findViewById<Button>(R.id.loadHabits).setOnClickListener {
            viewModel.intents.offer(HabitListIntent.Load)
        }

        viewModel.intents.offer(HabitListIntent.Load)

        launch(Main) {
            viewModel.states.consumeEach(::render)
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
        recyclerAdapter.destroy()
    }

    private fun render(viewState: HabitListViewState) {
        when {
            viewState.loading -> Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
            viewState.error != null -> Toast.makeText(context, viewState.error, Toast.LENGTH_LONG).show()
            else -> {
                habits.clear()
                habits.addAll(viewState.habits)
                recyclerAdapter.notifyDataSetChanged()
            }
        }
    }
}