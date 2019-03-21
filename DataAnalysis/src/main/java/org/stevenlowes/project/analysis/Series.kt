package org.stevenlowes.project.analysis

import javafx.scene.chart.XYChart
import javafx.scene.paint.Color
import org.stevenlowes.project.analysis.gui.DataLabel
import tornadofx.observable

data class Series(val name: String, val color: Color?, val data: Map<Long, Double>, val labels: List<DataLabel>) {
    fun toXYSeries(): XYChart.Series<Number, Number> =
        XYChart.Series<Number, Number>(
            data.map { (x, y) ->
                XYChart.Data<Number, Number>(x, y)
            }.observable()
        )
}
