package org.stevenlowes.project.analysis.gui

import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.layout.Priority
import org.stevenlowes.project.analysis.app.visualisations.Visualisation
import tornadofx.hgrow
import tornadofx.observable
import tornadofx.vgrow

class LabelledLineChart(vis: Visualisation) :
    LineChart<Number, Number>(
        NumberAxis(),
        NumberAxis(),
        vis.data.map { map ->
            map.toSeries()
        }.observable()
    ) {

    private val dataLabels: DataLabels = DataLabels(xAxis as NumberAxis, yAxis as NumberAxis, chartChildren, vis.labels)

    init{
        hgrow = Priority.ALWAYS
        vgrow = Priority.ALWAYS

        title = vis.title

        val xAxis = xAxis as NumberAxis
        val yAxis = yAxis as NumberAxis

        xAxis.label = vis.xLabel
        xAxis.isAutoRanging = true
        xAxis.isForceZeroInRange = false

        yAxis.label = vis.yLabel
        yAxis.isAutoRanging = true
        yAxis.isForceZeroInRange = false

        isLegendVisible = false
        createSymbols = false
        animated = false
        isVisible = true
    }

    override fun layoutPlotChildren() {
        super.layoutPlotChildren()
        this.dataLabels.layout()
    }
}

private fun Map<Long, Double>.toSeries(): XYChart.Series<Number, Number> =
    XYChart.Series<Number, Number>(
        map { (x, y) ->
            XYChart.Data<Number, Number>(x, y)
        }.observable()
    )