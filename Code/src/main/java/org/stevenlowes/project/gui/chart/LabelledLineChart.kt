package org.stevenlowes.project.gui.chart

import javafx.collections.FXCollections
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import tornadofx.*

open class LabelledLineChart(xAxis: NumberAxis,
                             yAxis: NumberAxis,
                             private val series: XYChart.Series<Number, Number>,
                             labels: List<DataLabel>) :
        LineChart<Number, Number>(
                xAxis,
                yAxis,
                FXCollections.observableArrayList(series)
                                 ) {

    private val yConverter = {x : Number ->
        series.data.firstOrNull { it.xValue.toDouble() >= x.toDouble() }?.yValue
    }

    var labelSize: Int
        get() {
            return dataLabels.fontSize
        }
        set(newValue) {
            dataLabels.fontSize = newValue
        }

    private val dataLabels: DataLabels = DataLabels(
            xAxis,
            yAxis,
            yConverter,
            chartChildren,
            labels)

    val labels get() = dataLabels.dataLabels.toList()

    fun addLabel(text: String, x: Number? = series.data.lastOrNull()?.xValue) {
        x ?: return

        runAsync {} ui {
            dataLabels.add(DataLabel(text, x))
        }
    }

    fun replaceAllLabels(labels: Iterable<DataLabel>) {
        runAsync {} ui {
            dataLabels.clear()
            dataLabels.addAll(labels)
        }
    }

    open fun clear() {
        series.data.clear()
        dataLabels.clear()
    }

    override fun layoutPlotChildren() {
        super.layoutPlotChildren()
        this.dataLabels.layout()
    }
}
