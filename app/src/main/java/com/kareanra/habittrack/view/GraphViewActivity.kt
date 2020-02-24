package com.kareanra.habittrack.view

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.Typeface.BOLD
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.kareanra.habittrack.R
import kotlinx.android.synthetic.main.activity_graph.*
import kotlin.random.Random

class GraphViewActivity : AppCompatActivity(), OnChartValueSelectedListener {

    private lateinit var tfLight: Typeface
    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_graph)

        tfLight = Typeface.create(Typeface.SERIF, BOLD)

        habit_chart.apply {
            setOnChartValueSelectedListener(this@GraphViewActivity)
            description.isEnabled = false
            setPinchZoom(true)
            setDrawBarShadow(false)
            setDrawGridBackground(false)
        }

        habit_chart.legend.apply {
            verticalAlignment = Legend.LegendVerticalAlignment.TOP
            horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            orientation = Legend.LegendOrientation.VERTICAL
            setDrawInside(true)
            typeface = tfLight
            yOffset = 0f
            xOffset = 10f
            yEntrySpace = 0f
            textSize = 12f
        }

        val dayRange = Day(DayOfWeek.SUNDAY, 23)..Day(DayOfWeek.SATURDAY, 29)
        val todayDayOfWeek = DayOfWeek.MONDAY

        habit_chart.xAxis.apply {
            typeface = tfLight
            granularity = 1f
            setLabelCount(7, true)
            setCenterAxisLabels(true)
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val day = dayRange[value.toInt()]
                    return if (day.dayOfWeek == todayDayOfWeek) {
                        "TODAY - ${day.format()}"
                    } else {
                        day.format()
                    }
                }
            }
        }

        habit_chart.axisLeft.apply {
            typeface = tfLight
            valueFormatter = LargeValueFormatter()
            setDrawGridLines(false)
            spaceTop = 35f
            axisMinimum = 0f
            axisMaximum = 10f
        }

        habit_chart.axisRight.isEnabled = false

        loadData(dayRange)
    }

    override fun onValueSelected(e: Entry, h: Highlight) {
        dismissDialog()
        dialog = AlertDialog.Builder(this)
            .setTitle("Notes for today")
            .setMessage("$e - $h")
            .setPositiveButton("Done") { _, _ ->
                dismissDialog()
            }
            .create()
            .also {
                it.show()
            }
    }

    override fun onNothingSelected() {
        dismissDialog()
    }

    private fun dismissDialog() {
        dialog?.dismiss()
        dialog = null
    }

    private fun loadData(days: List<Day>) {
        val values1 = mutableListOf<BarEntry>()
        val values2 = mutableListOf<BarEntry>()
        val values3 = mutableListOf<BarEntry>()
        val values4 = mutableListOf<BarEntry>()

        val randomMultiplier = 10f

        for (day in days) {
            values1.add(BarEntry(day.dayOfWeek.ordinal.toFloat(), Random.nextFloat() * randomMultiplier))
            values2.add(BarEntry(day.dayOfWeek.ordinal.toFloat(), Random.nextFloat() * randomMultiplier))
            values3.add(BarEntry(day.dayOfWeek.ordinal.toFloat(), Random.nextFloat() * randomMultiplier))
            values4.add(BarEntry(day.dayOfWeek.ordinal.toFloat(), Random.nextFloat() * randomMultiplier))
        }

        val set1: IBarDataSet
        val set2: IBarDataSet
        val set3: IBarDataSet
        val set4: IBarDataSet

        set1 = BarDataSet(values1, "Habit 1")
        set1.color = Color.rgb(104, 241, 175)
        set2 = BarDataSet(values2, "Habit 2")
        set2.color = Color.rgb(164, 228, 251)
        set3 = BarDataSet(values3, "Habit 3")
        set3.color = Color.rgb(242, 247, 158)
        set4 = BarDataSet(values4, "Habit 4")
        set4.color = Color.rgb(255, 102, 0)

        val data = BarData(set1, set2, set3, set4)
        data.setValueFormatter(LargeValueFormatter())
        data.setValueTypeface(tfLight)
        habit_chart.data = data

        // specify the width each bar should have
        habit_chart.barData.barWidth = 0.2f

        // restrict the x-axis range
        habit_chart.xAxis.axisMinimum = 0f

        // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
        habit_chart.xAxis.axisMaximum = habit_chart.barData.getGroupWidth(0.08f, 0.03f) * 7
        habit_chart.groupBars(0f, 0.08f, 0.03f)
        habit_chart.invalidate()
    }

    private enum class DayOfWeek(val display: String) {
        SUNDAY("Su"),
        MONDAY("M"),
        TUESDAY("T"),
        WEDNESDAY("W"),
        THURSDAY("Th"),
        FRIDAY("F"),
        SATURDAY("S");
    }

    private class Day(
        val dayOfWeek: DayOfWeek,
        val dayOfMonth: Int
    ) {
        operator fun rangeTo(other: Day) =
            (dayOfMonth..other.dayOfMonth).mapIndexed { index, newDayOfMonth ->
                val ordinal = (index + dayOfWeek.ordinal).rem(7)
                val newDayOfWeek = DayOfWeek.values()[ordinal]
                Day(newDayOfWeek, newDayOfMonth)
            }

        fun format() =
            "$dayOfMonth (${dayOfWeek.display})"
    }
}
