package org.stevenlowes.project.gui.datacollection

import gnu.io.CommPortIdentifier
import javafx.animation.AnimationTimer
import javafx.scene.chart.XYChart
import org.stevenlowes.project.gui.chart.GsrChart
import org.stevenlowes.project.gui.datascreenshot.DataScreenshot
import org.stevenlowes.project.gui.util.ListInput
import org.stevenlowes.project.serialreader.Serial
import tornadofx.*

class DataCollectionView : View("Data Collection") {
    private val chart = GsrChart()
    private val buffer = mutableListOf<XYChart.Data<Number, Number>>()
    private var pause = false
    private var serial: Serial? = null

    init {
        prepareTimeline()

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

        whenDocked {
            if(serial == null){
                val ports = CommPortIdentifier.getPortIdentifiers().asSequence().map { it as CommPortIdentifier }.toList()
                val port = ListInput(ports, CommPortIdentifier::getName).getInput()

                if(port == null){
                    workspace.navigateBack()
                }
                else {
                    serial = Serial(port) { reading ->
                        runAsync {
                            buffer(System.currentTimeMillis(), reading)
                        }
                    }
                }
            }
        }
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
