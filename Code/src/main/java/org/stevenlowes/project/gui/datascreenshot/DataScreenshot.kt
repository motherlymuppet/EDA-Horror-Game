package org.stevenlowes.project.gui.datascreenshot

import javafx.collections.ObservableList
import javafx.embed.swing.SwingFXUtils
import javafx.scene.Scene
import javafx.scene.SceneAntialiasing
import javafx.scene.SnapshotParameters
import javafx.scene.chart.XYChart
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import org.stevenlowes.project.gui.chart.GsrChart
import org.stevenlowes.project.png.Png
import tornadofx.*
import java.io.File
import java.nio.file.Path

class DataScreenshot private constructor(series: ObservableList<XYChart.Data<Number, Number>>) : View() {
    private val chart = GsrChart(series)

    init {
        chart.data.first().node.style {
            strokeWidth = Dimension(10.0, Dimension.LinearUnits.px)
        }
    }

    override val root = stackpane {
        add(chart)
    }

    companion object {
        private val snapshotParams = SnapshotParameters()
        private const val WIDTH = 1920.0
        private const val HEIGHT = 1080.0

        init {
            snapshotParams.fill = Color.valueOf("F4F4F4")
            snapshotParams.isDepthBuffer = true
        }

        fun screenshot(path: String, series: ObservableList<XYChart.Data<Number, Number>>) {
            val view = DataScreenshot(series)
            val chart = view.root
            Scene(chart, WIDTH, HEIGHT, true, SceneAntialiasing.BALANCED)

            val tempImage = chart.snapshot(snapshotParams, null)
            val image = SwingFXUtils.fromFXImage(tempImage, null)

            val text: String = series.asSequence().map {
                "${it.xValue},${it.yValue}"
            }.joinToString("\n")

            runAsync {
                Png.write(path, image, text)
            }
        }

        fun screenshot(series: ObservableList<XYChart.Data<Number, Number>>) {
            val file = chooseFile("Save Location",
                                  listOf(FileChooser.ExtensionFilter("Png Images", "*.png")).toTypedArray(),
                                  FileChooserMode.Save
                                 ) {
                initialDirectory = File(System.getProperty("user.dir"))
            }.firstOrNull()

            if (file != null) {
                screenshot(file.absolutePath, series)
            }
        }
    }
}