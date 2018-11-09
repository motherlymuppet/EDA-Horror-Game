package org.stevenlowes.project.gui.datacollection

import gnu.io.CommPortIdentifier
import javafx.animation.AnimationTimer
import javafx.scene.chart.XYChart
import org.stevenlowes.project.gui.dataexplorer.DataExplorerView
import org.stevenlowes.project.gui.inputmodals.ListInput
import org.stevenlowes.project.gui.inputmodals.TextInput
import tornadofx.*
import java.util.concurrent.ConcurrentLinkedQueue

class DataCollectionView : View("Data Collection") {
    private val chart: DataCollectionChart = DataCollectionChart(port!!)
    private val buffer = ConcurrentLinkedQueue<XYChart.Data<Number, Number>>()

    companion object {
        var port: CommPortIdentifier? = null
    }

    init {
        whenDeleted {
            chart.clear()
            buffer.clear()
        }

        whenSaved {
            workspace.dock(DataExplorerView(rawData = chart.series.map { it.xValue.toLong() to it.yValue.toDouble() },
                                            labels = chart.labels))
        }

        whenCreated {
            val last = chart.series.lastOrNull() ?: return@whenCreated
            val labelText = TextInput("Enter Label").getInputBlankIsNull() ?: return@whenCreated
            val x = last.xValue
            chart.add(labelText, x)
        }

        whenDocked {
            chart.start()
        }

        whenUndocked {
            chart.stop()
        }

        disableRefresh()
    }

    override val root = chart
}
