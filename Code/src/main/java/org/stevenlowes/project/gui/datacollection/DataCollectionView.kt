package org.stevenlowes.project.gui.datacollection

import javafx.animation.AnimationTimer
import javafx.scene.chart.XYChart
import org.stevenlowes.project.gui.chart.GsrChart
import org.stevenlowes.project.gui.datascreenshot.DataScreenshot
import org.stevenlowes.project.serialreader.Serial
import tornadofx.*

class DataCollectionView : View("Data Collection") {
    private val comPort = 4
    private val chart = GsrChart()
    private val buffer = mutableListOf<XYChart.Data<Number, Number>>()
    private var pause = false

    init {
        prepareTimeline()

        Serial(comPort) { port ->
            runAsync {
                buffer(System.currentTimeMillis(), port)
            }
        }

        whenDeleted {
            chart.series.clear()
            buffer.clear()
        }

        whenSaved {
            pause = true
            DataScreenshot.screenshot(chart.series)
            pause = false
        }

        disableCreate()
        disableRefresh()
    }

    private fun buffer(time: Long, serialValue: Int) {
        buffer.add(XYChart.Data(time, serialValue))
    }

    private fun emptyBuffer() {
        if (!pause) {
            chart.series.addAll(buffer.filter { it.yValue.toDouble() > 1.0 })
            buffer.clear()
        }
    }

    private fun prepareTimeline() {
        // Every frame to take any data from queue and add to chart
        object : AnimationTimer() {
            override fun handle(now: Long) {
                emptyBuffer()
            }
        }.start()
    }

    override fun onBeforeShow() {
        workspace.dock<DataCollectionView>()
    }

    override val root = chart
}