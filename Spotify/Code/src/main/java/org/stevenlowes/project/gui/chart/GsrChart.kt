@file:Suppress("NestedLambdaShadowedImplicitParameter")

package org.stevenlowes.project.gui.chart

import javafx.beans.InvalidationListener
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.layout.Priority
import tornadofx.*
import java.lang.Math.round
import java.time.ZoneOffset

open class GsrChart(val autoLowerBound: AutoLowerBound = AutoLowerBound.AUTOMATIC,
                    val series: ObservableList<XYChart.Data<Number, Number>> = FXCollections.observableArrayList<XYChart.Data<Number, Number>>(),
                    labels: List<DataLabel> = listOf()) :
        LabelledLineChart(
                NumberAxis(),
                NumberAxis(),
                XYChart.Series(SERIES_NAME, series),
                labels) {

    companion object {
        val ZONE_OFFSET = ZoneOffset.UTC
        val timeConverter = ChartTimeConverter(ZONE_OFFSET)
        private const val SERIES_NAME = "Relaxation over Time"
        private const val X_LABEL = "Time"
        private const val Y_LABEL = "Relaxation"
    }

    private fun round1000(number: Number): Double {
        return (round(number.toDouble() / 1000) * 1000).toDouble()
    }

    init {
        hgrow = Priority.ALWAYS
        vgrow = Priority.ALWAYS

        xAxis.label = X_LABEL
        xAxis.isAutoRanging = false
        xAxis.tickLabelFormatter = timeConverter
        xAxis.isForceZeroInRange = false

        series.addListener(InvalidationListener {
            if (series.isNotEmpty()) {
                xAxis.upperBound = series.last().xValue.toDouble()

                if (autoLowerBound == AutoLowerBound.AUTOMATIC) {
                    xAxis.lowerBound = series.first().xValue.toDouble()
                }
                else if (autoLowerBound == AutoLowerBound.FIVE_MINUTES) {
                    xAxis.lowerBound = xAxis.upperBound - (5 * 60 * 1000)
                }

                xAxis.tickUnit = (xAxis.upperBound - xAxis.lowerBound) / 10

                val yValues = series.asSequence().filter { it.xValue.toDouble() >= xAxis.lowerBound && it.xValue.toDouble() <= xAxis.upperBound }.map { it.yValue.toDouble() }.toList()
                yAxis.lowerBound = yValues.min()!!
                yAxis.upperBound = yValues.max()!!
                yAxis.tickUnit = yAxis.upperBound - (yAxis.lowerBound / 10)
            }
        })

        series.invalidate()

        yAxis.label = Y_LABEL
        yAxis.isAutoRanging = false
        yAxis.isForceZeroInRange = false

        isLegendVisible = false
        createSymbols = false
        animated = false
        isVisible = true
    }

    override fun getXAxis(): NumberAxis {
        return super.getXAxis() as NumberAxis
    }

    override fun getYAxis(): NumberAxis {
        return super.getYAxis() as NumberAxis
    }

    fun addData(data: List<Pair<Long, Double>>) {
        series.addAll(
                data.map { (time, reading) ->
                    XYChart.Data<Number, Number>(time, reading)
                }
                     )
    }
}