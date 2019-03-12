package org.stevenlowes.project.analysis.gui

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.chart.NumberAxis
import tornadofx.observable

class DataLabels(private val xAxis: NumberAxis, private val yAxis: NumberAxis,
    chartChildren: ObservableList<Node>,
                 labels: List<DataLabel>) {

    private val dataLabels: ObservableList<DataLabel> = labels.observable()
    var fontSize: Int = 12

    init {
        val plotArea = chartChildren[1] as Group
        if (plotArea.children.isEmpty()) {
            throw IllegalStateException("plotArea is empty!")
        }
        val plotContent = plotArea.children.last() as Group
        val group = Group(dataLabels.map { it.node })
        plotArea.children.add(group)

        // Sync the group and background layout to the plotContent
        group.layoutXProperty().bind(plotContent.layoutXProperty())
        group.layoutYProperty().bind(plotContent.layoutYProperty())

        group.requestLayout()
        plotContent.requestLayout()
    }

    fun layout() {
        dataLabels.forEach { it.layout(fontSize, xAxis, yAxis) }
    }
}
