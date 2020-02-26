package com.kareanra.habittrack.model.view

import com.kareanra.habittrack.model.InputType

data class HabitView(
    val id: Long,
    val inputType: InputType,
    val name: String
)

data class AnswerView(
    val yyyymmdd: Int,
    val value: Int,
    val notes: String
) {
    fun isEmpty() =
        this === empty

    companion object {
        private val empty =
            AnswerView(
                yyyymmdd = 0,
                value = -1,
                notes = ""
            )

        fun empty() =
            empty
    }
}

data class AnswerableHabitView(
    val habit: HabitView,
    val answer: AnswerView?
) {
    fun habitId() =
        habit.id

    val inputType
        get() = habit.inputType

    fun formattedAnswer() =
        when (inputType) {
            InputType.RANGE, InputType.NUMERICAL -> answer?.value.toString()
            InputType.YES_NO -> answer?.value?.let { if (it == 0) "No" else "Yes" }
        }
}
