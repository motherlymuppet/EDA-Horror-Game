package org.stevenlowes.project.gui.chart

import javafx.beans.InvalidationListener
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.layout.Priority
import tornadofx.*
import java.time.ZoneOffset

open class GsrChart(val autoLowerBound: AutoLowerBound = AutoLowerBound.AUTOMATIC, val series: ObservableList<XYChart.Data<Number, Number>> = FXCollections.observableArrayList<XYChart.Data<Number, Number>>()) : LineChart<Number, Number>(
        NumberAxis(),
        NumberAxis(),
        FXCollections.observableArrayList(XYChart.Series(SERIES_NAME, series))){

    companion object {
        val ZONE_OFFSET = ZoneOffset.ofHours(-1)
        val timeConverter = ChartTimeConverter(ZONE_OFFSET)
        private const val SERIES_NAME = "Relaxation over Time"
        private const val X_LABEL = "Time"
        private const val Y_LABEL = "Relaxation"
    }

    init {
        hgrow = Priority.ALWAYS
        vgrow = Priority.ALWAYS

        xAxis.label = X_LABEL
        xAxis.isAutoRanging = false
        xAxis.tickLabelFormatter = timeConverter
        xAxis.isForceZeroInRange = false

        series.addListener(InvalidationListener {
            if(series.isNotEmpty()){
                if(autoLowerBound == AutoLowerBound.AUTOMATIC){
                    xAxis.lowerBound = series.first().xValue.toDouble()
                }
                xAxis.upperBound = series.last().xValue.toDouble()
                xAxis.tickUnit = (xAxis.upperBound - xAxis.lowerBound)/10
            }
        })

        series.invalidate()

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