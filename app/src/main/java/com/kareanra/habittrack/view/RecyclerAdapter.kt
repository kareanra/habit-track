package com.kareanra.habittrack.view

import android.util.Log
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

class RecyclerAdapter(
    private val habits: List<Habit>
) : RecyclerView.Adapter<RecyclerAdapter.TextViewHolder>() {

    private val _userUpdates = Channel<RecyclerViewIntent>(UNLIMITED)
    val userUpdates: ReceiveChannel<RecyclerViewIntent>
        get() = _userUpdates

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
        return TextViewHolder(parent.inflate(R.layout.recyclerview_item_row), _userUpdates)
    }

    override fun getItemCount(): Int = habits.size

    override fun onBindViewHolder(holder: TextViewHolder, position: Int) {
        val habit = habits[position]
        holder.bind(habit)
    }

    class TextViewHolder(
        v: View,
        private val userUpdates: Channel<RecyclerViewIntent>
    ) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        private var habit: Habit? = null

        init {
            v.setOnClickListener(this)
            // TODO: navigate to detail screen
        }

        override fun onClick(v: View) {
            Log.i("RecyclerView", "CLICK!")
            habit?.let { userUpdates.offer(RecyclerViewIntent.HabitDetail(it.id)) }
        }

        fun bind(habit: Habit) {
            this.habit = habit
            view.habitName.text = habit.name
        }
    }

    fun destroy() = _userUpdates.cancel()
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

sealed class RecyclerViewIntent {
    class HabitDetail(val id: Long) : RecyclerViewIntent()
}