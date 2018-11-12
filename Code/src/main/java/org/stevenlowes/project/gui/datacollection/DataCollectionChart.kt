package org.stevenlowes.project.gui.datacollection

import gnu.io.CommPortIdentifier
import javafx.animation.AnimationTimer
import javafx.scene.chart.XYChart
import org.stevenlowes.project.gui.chart.AutoLowerBound
import org.stevenlowes.project.gui.chart.GsrChart
import org.stevenlowes.project.serialreader.Serial
import tornadofx.*
import java.util.concurrent.ConcurrentLinkedQueue

class DataCollectionChart(autoLowerBound: AutoLowerBound = AutoLowerBound.AUTOMATIC) : GsrChart(autoLowerBound) {
    var paused = false
        private set

    private val buffer = ConcurrentLinkedQueue<XYChart.Data<Number, Number>>()

    private var serial = Serial { reading ->
        runAsync {
            if (!paused) {
                buffer(System.currentTimeMillis(), reading)
            }
        }
    }

    init {
        prepareTimeline()
    }

    fun start() {
        paused = false
    }

    fun stop() {
        paused = true
        buffer.clear()
        clear()
    }

    fun close() {
        serial.close()
        buffer.clear()
        clear()
    }

    private fun buffer(time: Long, serialValue: Int) {
        buffer.add(XYChart.Data(time, serialValue))
    }

    private fun emptyBuffer() {
        series.addAll(buffer)
        buffer.clear()
    }

    private fun prepareTimeline() {
        // Every frame to take any data from queue and addLabel to chart
        object : AnimationTimer() {
            override fun handle(now: Long) {
                emptyBuffer()
            }
        }.start()
    }
}
