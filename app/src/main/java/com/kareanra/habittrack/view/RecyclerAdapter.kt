package com.kareanra.habittrack.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.kareanra.habittrack.R
import com.kareanra.habittrack.model.Habit
import kotlinx.android.synthetic.main.recyclerview_item_row.view.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

class RecyclerAdapter(
    private val habits: List<Habit>
) : RecyclerView.Adapter<RecyclerAdapter.RowViewHolder>() {

    private val _userUpdates = Channel<RecyclerViewIntent>(UNLIMITED)
    val userUpdates: ReceiveChannel<RecyclerViewIntent>
        get() = _userUpdates

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        return RowViewHolder(parent.inflate(R.layout.recyclerview_item_row), _userUpdates)
    }

    override fun getItemCount(): Int = habits.size

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        val habit = habits[position]
        holder.bind(habit)
    }

    class RowViewHolder(
        v: View,
        private val userUpdates: SendChannel<RecyclerViewIntent>
    ) : RecyclerView.ViewHolder(v) {
        private var row: View = v
        private var habit: Habit? = null // TODO: lateinit

        init {
            setUpClickListeners()
        }

        fun bind(habit: Habit) {
            this.habit = habit
            row.habitName.text = habit.name
        }

        private fun setUpClickListeners() {
            row.habitName.setOnClickListener {
                habit?.let {
                    userUpdates.offer(RecyclerViewIntent.HabitClicked(it.id))
                }
            }

            row.edit_habit.setOnClickListener {
                habit?.let {
                    userUpdates.offer(RecyclerViewIntent.HabitEditClicked(it.id))
                }
            }
        }
    }

    fun destroy() = _userUpdates.cancel()
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

sealed class RecyclerViewIntent {
    class HabitClicked(val id: Long) : RecyclerViewIntent()
    class HabitEditClicked(val id: Long) : RecyclerViewIntent()
}
