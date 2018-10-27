package org.stevenlowes.project.gui.datacollection

import javafx.animation.AnimationTimer
import javafx.geometry.Pos
import javafx.scene.chart.XYChart
import org.stevenlowes.project.gui.chart.GsrChart
import org.stevenlowes.project.gui.datascreenshot.DataScreenshot
import org.stevenlowes.project.serialreader.Serial
import tornadofx.*

class DataCollectionFragment(comPort: Int) : Fragment(){
    init {
        Serial(comPort) {
            runAsync {
                buffer(System.currentTimeMillis(), it)
            }
        }

        prepareTimeline()
    }

    private val chart = GsrChart()
    private val buffer = mutableListOf<XYChart.Data<Number, Number>>()

    private fun buffer(time: Long, serialValue: Int){
        buffer.add(XYChart.Data(time, serialValue))
    }

    private fun emptyBuffer(){
        chart.series.addAll(buffer.filter { it.yValue.toDouble() > 1.0 })
        buffer.clear()
    }

    private fun prepareTimeline() {
        // Every frame to take any data from queue and add to chart
        object : AnimationTimer() {
            override fun handle(now: Long) {
                emptyBuffer()
            }
        }.start()
    }

    override val root = borderpane{
        center = chart

        bottom {
            button {
                text = "Save"
                alignment = Pos.CENTER
                action {
                    DataScreenshot.screenshot(chart.series)
                }
            }
        }
    }
}