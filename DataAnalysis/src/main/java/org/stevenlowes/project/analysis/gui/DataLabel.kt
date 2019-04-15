package org.stevenlowes.project.analysis.gui

import javafx.geometry.Pos
import javafx.scene.chart.NumberAxis
import javafx.scene.control.Label
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import jdk.nashorn.internal.ir.annotations.Ignore

class DataLabel constructor(
    val text: String,
    val x: Long,
    val y: Double,
    @Transient val textAnchor: Pos = Pos.CENTER
) {
    val node = Label(text)

    fun layout(fontSize: Int, xAxis: NumberAxis, yAxis: NumberAxis) {
        val family = node.font.family
        node.font = Font.font(family, FontWeight.BOLD, fontSize.toDouble())
        node.isVisible = true

        val displayX = xAxis.getDisplayPosition(x)
        val displayY = yAxis.getDisplayPosition(y)

        layoutText(displayX, displayY)
    }

    private fun layoutText(displayX: Double, displayY: Double) {
        // Initially the node width and height are 0 so we have to recompute the layout after the first rendering.  See the width and height property listeners.
        when (textAnchor) {
            Pos.TOP_CENTER, Pos.CENTER, Pos.BOTTOM_CENTER -> node.layoutX = displayX - node.width / 2
            Pos.TOP_LEFT, Pos.CENTER_LEFT, Pos.BOTTOM_LEFT -> node.layoutX = displayX
            Pos.TOP_RIGHT, Pos.CENTER_RIGHT, Pos.BOTTOM_RIGHT -> node.layoutX = displayX - node.width
            else -> throw IllegalStateException("${textAnchor.name} is not supported.")
        }
        when (textAnchor) {
            Pos.CENTER, Pos.CENTER_LEFT, Pos.CENTER_RIGHT -> node.layoutY = displayY - node.height / 2
            Pos.TOP_LEFT, Pos.TOP_CENTER, Pos.TOP_RIGHT -> node.layoutY = displayY
            Pos.BOTTOM_LEFT, Pos.BOTTOM_CENTER, Pos.BOTTOM_RIGHT -> node.layoutY = displayY - node.height
            else -> throw IllegalStateException("${textAnchor.name} is not supported.")
        }
    }
}
