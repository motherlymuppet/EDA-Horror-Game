package org.stevenlowes.project.gui.datascreenshot

import javafx.collections.ObservableList
import javafx.embed.swing.SwingFXUtils
import javafx.scene.Scene
import javafx.scene.SceneAntialiasing
import javafx.scene.SnapshotParameters
import javafx.scene.chart.XYChart
import javafx.scene.paint.Color
import org.stevenlowes.project.gui.chart.GsrChart
import tornadofx.*
import java.io.File
import javax.imageio.ImageIO

class DataScreenshot private constructor(series: ObservableList<XYChart.Data<Number, Number>>): View() {
    override val root = stackpane {
        add(GsrChart(series))
    }

    companion object {
        private val snapshotParams = SnapshotParameters()
        private const val WIDTH = 1920.0
        private const val HEIGHT = 1080.0

        init {
            snapshotParams.fill = Color.valueOf("F4F4F4")
            snapshotParams.isDepthBuffer = true
        }

        fun screenshot(path: String, series: ObservableList<XYChart.Data<Number, Number>>){
            val view = DataScreenshot(series)
            val chart = view.root
            Scene(chart, WIDTH, HEIGHT, true, SceneAntialiasing.BALANCED)

            val tempImage = chart.snapshot(snapshotParams, null)
            val image = SwingFXUtils.fromFXImage(tempImage, null)

            val file = File(path)
            ImageIO.write(image, "png", file)
        }

        fun screenshot(series: ObservableList<XYChart.Data<Number, Number>>){
            val view = PathEntryView()
            view.openModal(block = true)
            val name = view.name.get()
            if(!name.isNullOrBlank()){
                screenshot("$name.png", series)
            }
        }
    }
}