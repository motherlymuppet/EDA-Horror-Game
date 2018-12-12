package org.stevenlowes.project.gui.datacollection

import gnu.io.CommPortIdentifier
import javafx.animation.AnimationTimer
import javafx.scene.chart.XYChart
import org.stevenlowes.project.gui.chart.AutoLowerBound
import org.stevenlowes.project.gui.chart.GsrChart
import org.stevenlowes.project.serialreader.SERIAL
import org.stevenlowes.project.serialreader.Serial
import tornadofx.*
import java.util.concurrent.ConcurrentLinkedQueue

class DataCollectionChart(autoLowerBound: AutoLowerBound = AutoLowerBound.AUTOMATIC) : GsrChart(autoLowerBound) {
    init {
        prepareTimeline()
    }

    fun start(){
        SERIAL.paused = false
    }

    fun stop() {
        SERIAL.paused = true
        clear()
    }

    private fun prepareTimeline() {
        // Every frame to take any data from queue and addLabel to chart
        object : AnimationTimer() {
            override fun handle(now: Long) {
                addData(SERIAL.consume())
            }
        }.start()
    }
}
