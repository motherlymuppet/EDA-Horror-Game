package org.stevenlowes.project.analysis.gui

import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.layout.Priority
import org.stevenlowes.project.analysis.Config
import org.stevenlowes.project.analysis.app.visualisations.Visualisation
import tornadofx.hgrow
import tornadofx.observable
import tornadofx.runLater
import tornadofx.vgrow
import javafx.collections.ListChangeListener
import com.sun.javafx.charts.Legend
import javafx.application.Platform
import javafx.scene.control.Label
import org.stevenlowes.project.analysis.hex


class LabelledLineChart(vis: Visualisation) :
    LineChart<Number, Number>(
        NumberAxis(),
        NumberAxis(),
        vis.series.map { series ->
            series.xySeries
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

        isLegendVisible = Config.showLegend
        createSymbols = false
        animated = false
        isVisible = true

        vis.series.forEach { it.updateColor() }

        Platform.runLater {
            (legend as Legend)
                .items
                .map { it.symbol }
                .zip(vis.series)
                .filter { (_, series) -> series.color != null }
                .forEach {(symbol, series) ->
                symbol.style = "-fx-background-color: ${series.color!!.hex};"
            }
        }
    }

    override fun layoutPlotChildren() {
        super.layoutPlotChildren()
        this.dataLabels.layout()
    }
}