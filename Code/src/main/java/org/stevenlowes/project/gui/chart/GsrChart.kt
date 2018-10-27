package org.stevenlowes.project.gui.chart

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import java.time.ZoneOffset

open class GsrChart(val series: ObservableList<XYChart.Data<Number, Number>> = FXCollections.observableArrayList<XYChart.Data<Number, Number>>()) : LineChart<Number, Number>(
        NumberAxis(),
        NumberAxis(),
        FXCollections.observableArrayList(XYChart.Series(SERIES_NAME, series))){

    companion object {
        private const val SERIES_NAME = "Relaxation over Time"
        private val ZONE_OFFSET = ZoneOffset.ofHours(-1)
        private const val X_LABEL = "Time"
        private const val Y_LABEL = "Relaxation"
    }

    init {
        xAxis.label = X_LABEL
        xAxis.isAutoRanging = true
        xAxis.tickLabelFormatter = ChartTimeConverter(ZONE_OFFSET)
        xAxis.isForceZeroInRange = false

        yAxis.label = Y_LABEL
        yAxis.isAutoRanging = true
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
}