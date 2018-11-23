package org.stevenlowes.project.gui.chart

import javafx.beans.InvalidationListener
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.chart.NumberAxis

class DataLabels(private val xAxis: NumberAxis,
                 private val yAxis: NumberAxis,
                 private val yConverter: (Number) -> Number?,
                 chartChildren: ObservableList<Node>,
                 labels: List<DataLabel>) {
    private val plotContent: Group
    private val group = Group()
    val dataLabels: ObservableList<DataLabel> = FXCollections.observableArrayList<DataLabel>(labels)
    var fontSize: Int = 24

    init {
        val plotArea = chartChildren[1] as Group
        if (plotArea.children.isEmpty()) {
            throw IllegalStateException("plotArea is empty!")
        }
        plotContent = plotArea.children.last() as Group
        plotArea.children.add(group)

        // Sync the group and background layout to the plotContent
        group.layoutXProperty().bind(plotContent.layoutXProperty())
        group.layoutYProperty().bind(plotContent.layoutYProperty())

        // Re-layout the dataLabels when the dataLabels change
        dataLabels.addListener(InvalidationListener { layout() })
    }

    fun add(label: DataLabel) {
        dataLabels.add(label)
        group.children.add(label.node)
        group.requestLayout()
        plotContent.requestLayout()
    }

    fun addAll(labels: Iterable<DataLabel>){
        dataLabels.addAll(labels)
        group.children.addAll(labels.map { it.node })
        group.requestLayout()
        plotContent.requestLayout()
    }

    fun remove(label: DataLabel) {
        group.children.remove(label.node)
        dataLabels.remove(label)
    }

    fun clear() {
        group.children.removeAll(dataLabels.map { it.node })
        dataLabels.clear()
    }

    fun layout() {
        dataLabels.forEach { it.layout(fontSize, xAxis, yAxis, yConverter) }
    }
}
