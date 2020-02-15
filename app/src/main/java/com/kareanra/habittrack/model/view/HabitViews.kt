package com.kareanra.habittrack.model.view

data class HabitView(
    val id: Long,
    val name: String
)

data class AnswerView(
    val yyyymmdd: Int,
    val value: Int,
    val notes: String
)

data class AnswerableHabitView(
    val habit: HabitView,
    val answer: AnswerView?
) {
    fun habitId() =
        habit.id

    fun isAnswered() =
        answer != null
}
