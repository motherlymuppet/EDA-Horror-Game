package org.stevenlowes.project.analysis

import javafx.application.Platform
import javafx.scene.chart.XYChart
import javafx.scene.paint.Color
import org.stevenlowes.project.analysis.gui.DataLabel
import tornadofx.observable

data class Series(val name: String, val color: Color?, val data: Map<Long, Double>, val labels: List<DataLabel>) {
    val xySeries: XYChart.Series<Number, Number> by lazy {
        val series = XYChart.Series<Number, Number>(
            destructedData.map { (x, y) ->
                XYChart.Data<Number, Number>(x, y)
            }.observable()
        )
        series.name = name
        return@lazy series
    }

    fun updateColor(){
        if(color != null) {
            Platform.runLater {
                xySeries.node.style = "-fx-stroke: ${color.hex};"
            }
        }
    }

    val destructedData: Map<Long, Double> by lazy {
        val keepEvery = kotlin.math.max(data.size / 1000, 1)
        val keepKeys =
            data.keys.asSequence().sorted().withIndex().filter { it.index % keepEvery == 0 }.map { it.value }.toSet()
        return@lazy data.filterKeys { it in keepKeys }
    }

    fun withName(name: String) = Series(name, color, data, labels)
    fun withColor(color: Color?) = Series(name, color, data, labels)
    fun withData(data: Map<Long, Double>) = Series(name, color, data, labels)
    fun withLabels(labels: List<DataLabel>) = Series(name, color, data, labels)
}
