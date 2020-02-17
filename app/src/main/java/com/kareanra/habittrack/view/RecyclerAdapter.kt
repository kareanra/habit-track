package com.kareanra.habittrack.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.kareanra.habittrack.R
import com.kareanra.habittrack.model.InputType
import com.kareanra.habittrack.model.view.AnswerableHabitView
import kotlinx.android.synthetic.main.recyclerview_item_row.view.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

class RecyclerAdapter(
    private val habits: List<AnswerableHabitView>
) : RecyclerView.Adapter<RecyclerAdapter.RowViewHolder>() {

    private val _userUpdates = Channel<RecyclerViewIntent>(UNLIMITED)
    val userUpdates: ReceiveChannel<RecyclerViewIntent> // TODO: flow
        get() = _userUpdates

    private val holders = mutableListOf<RowViewHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RowViewHolder(parent.inflate(R.layout.recyclerview_item_row), _userUpdates).also {
            holders.add(it)
        }

    override fun getItemCount(): Int = habits.size

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        val habit = habits[position]
        holder.bind(habit)
    }

    fun redraw(state: RecyclerViewState) {
        // TODO: fix sliding down
        state.selectedHabit?.let { sel ->
            holders
                .filterNot { it.habit.habitId() == state.selectedHabit }
                .forEach {
                    it.renderComponents()
                    it.slideUp()
                }

            holders.first { it.habit.habitId() == sel }
                .slideDown()
        } ?: run {
            holders.forEach {
                it.slideUp()
            }
        }
    }

    class RowViewHolder(
        v: View,
        private val userUpdates: SendChannel<RecyclerViewIntent>
    ) : RecyclerView.ViewHolder(v) {
        private var row: View = v
        internal lateinit var habit: AnswerableHabitView

        init {
            setUpClickListeners()
        }

        fun bind(habit: AnswerableHabitView) {
            this.habit = habit

            row.habit_name.text =
                if (habit.answer != null) {
                    "${habit.habit.name} (Answered ${habit.answer.yyyymmdd}: ${habit.formattedAnswer()})"
                } else {
                    habit.habit.name
                }

            renderComponents()
        }

        fun renderComponents() {
            when (habit.inputType) {
                InputType.RANGE -> {
                    // TODO: dynamically set max
                    row.habit_numerical_input.visibility = View.GONE
                    row.habit_seek_bar.progress = habit.answer?.value ?: 0
                    row.habit_seek_bar.visibility = View.VISIBLE
                }
                InputType.YES_NO -> {
                    row.habit_seek_bar.visibility = View.GONE
                    row.habit_numerical_input.visibility = View.GONE
                }
                InputType.NUMERICAL -> {
                    row.habit_seek_bar.visibility = View.GONE
                    row.habit_numerical_input.visibility = View.VISIBLE
                    habit.answer?.let {
                        row.habit_numerical_input.setText(it.value.toString())
                    }
                }
            }
            habit.answer?.let {
                row.habit_notes.setText(it.notes)
            }
        }

        private fun setUpClickListeners() {
            row.habit_name.setOnClickListener {
                userUpdates.offer(RecyclerViewIntent.HabitClicked(habit.habitId()))
            }

            row.save_habit.setOnClickListener {
                userUpdates.offer(
                    RecyclerViewIntent.HabitSaveClicked(
                        id = habit.habitId(),
                        inputType = habit.inputType,
                        answer = row.habit_seek_bar.progress,
                        notes = row.habit_notes.text.toString()
                    )
                )
            }
        }

        internal fun slideDown(durationMillis: Long = 500) =
            row.habit_editing_pane.run {
                visibility = View.VISIBLE
                alpha = 0.0f
                animate()
                    .setDuration(durationMillis)
                    .translationY(height.toFloat())
                    .alpha(1.0f) // TODO: remove
            }

        internal fun slideUp(durationMillis: Long = 100) =
            row.habit_editing_pane.run {
                animate()
                    .setDuration(durationMillis)
                    .translationY(0.0f)
                    .alpha(0.0f)
                    .withEndAction { visibility = View.GONE }
            }
    }
    
    fun destroy() =
        _userUpdates.cancel()
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

sealed class RecyclerViewIntent {
    class HabitClicked(val id: Long) : RecyclerViewIntent()
    class HabitSaveClicked(
        val id: Long,
        val inputType: InputType,
        val answer: Int,
        val notes: String
    ) : RecyclerViewIntent()
}

data class RecyclerViewState(
    val selectedHabit: Long?
)
