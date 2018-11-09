package org.stevenlowes.project.gui.chart

import javafx.collections.FXCollections
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart

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

    private val dataLabels: DataLabels = DataLabels(
            xAxis,
            yAxis,
            yConverter,
            chartChildren,
            labels)

    val labels get() = dataLabels.dataLabels.toList()

    fun add(text: String, x: Number) {
        val label = DataLabel(text, x)
        dataLabels.add(label)
    }

    fun addAll(labels: Iterable<DataLabel>){
        dataLabels.addAll(labels)
    }

    fun clear() {
        series.data.clear()
        dataLabels.clear()
    }

    override fun layoutPlotChildren() {
        super.layoutPlotChildren()
        this.dataLabels.layout()
    }
}
