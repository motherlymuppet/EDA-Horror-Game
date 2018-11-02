package org.stevenlowes.project.gui.datascreenshot

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import javafx.collections.FXCollections
import javafx.embed.swing.SwingFXUtils
import javafx.scene.Scene
import javafx.scene.SceneAntialiasing
import javafx.scene.SnapshotParameters
import javafx.scene.chart.XYChart
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import org.stevenlowes.project.gui.chart.GsrChart
import org.stevenlowes.project.gui.dataexplorer.DataExplorerView
import org.stevenlowes.project.gui.dataexplorer.converters.DataPointConverter
import org.stevenlowes.project.png.Png
import tornadofx.*
import java.io.File

class DataScreenshot {
    companion object {
        private val snapshotParams = SnapshotParameters()
        private const val WIDTH = 1920.0
        private const val HEIGHT = 1080.0

        init {
            snapshotParams.fill = Color.valueOf("F4F4F4")
            snapshotParams.isDepthBuffer = true
        }

        /**
         * If path is null, a file chooser will pop up
         * If title is null, a text input will pop up
         * If title is blank (empty space only), no title will be added
         */
        fun screenshot(series: List<Pair<Long, Double>>,
                       chartTitle: String,
                       chartDescription: String,
                       chartConverters: List<DataPointConverter>,

                       path: String? = null) {
            val file =
                    if (path == null) {
                        chooseFile("Save Location",
                                   listOf(FileChooser.ExtensionFilter("Png Images", "*.png")).toTypedArray(),
                                   FileChooserMode.Save
                                  ) {
                            initialDirectory = File(System.getProperty("user.dir"))

                            initialFileName = chartTitle
                        }.firstOrNull()
                    }
                    else {
                        File(path)
                    } ?: return

            doScreenshot(file, series, chartTitle, chartDescription, chartConverters)
        }

        private fun doScreenshot(file: File,
                                 rawData: List<Pair<Long, Double>>,
                                 chartTitle: String,
                                 chartDescription: String,
                                 chartConverters: List<DataPointConverter>) {
            val data = DataExplorerView.applyConverters(rawData, chartConverters)
            val chart = GsrChart(series = FXCollections.observableArrayList(
                    data.map { (time, value) -> XYChart.Data(time as Number, value as Number) }))

            Scene(chart, WIDTH, HEIGHT, true, SceneAntialiasing.BALANCED)

            chart.style {
                fontSize = Dimension(36.0, Dimension.LinearUnits.pt)
            }
            chart.data.first().node.style {
                strokeWidth = Dimension(10.0, Dimension.LinearUnits.px)
            }

            val tempImage = chart.snapshot(snapshotParams, null)
            val image = SwingFXUtils.fromFXImage(tempImage, null)

            val json = Gson()

            val rootJson = JsonObject()
            rootJson.addProperty("Title", chartTitle)
            rootJson.addProperty("Description", chartDescription)

            val rawDataJson = JsonArray()
            rawData.forEach { (time, value) ->
                val datapointJson = JsonObject()
                datapointJson.addProperty("Millis", time)
                datapointJson.addProperty("Reading", value)
                rawDataJson.add(datapointJson)
            }
            rootJson.add("Data", rawDataJson)

            val convertersJson = JsonArray()
            chartConverters.forEach {
                val converterJson = it.toJson()
                convertersJson.add(converterJson)
            }
            rootJson.add("Converters", convertersJson)

            val text = json.toJson(rootJson)
            runAsync {
                Png.write(file.absolutePath, image, text)
            }
        }

        fun exploreScreenshot(): DataExplorerView? {
            val file = chooseFile("Select Image",
                                  listOf(FileChooser.ExtensionFilter("Png Images", "*.png")).toTypedArray(),
                                  FileChooserMode.Single
                                 ) {
                initialDirectory = File(System.getProperty("user.dir"))
            }.firstOrNull() ?: return null
            return exploreScreenshot(file)
        }

        private fun exploreScreenshot(file: File): DataExplorerView {
            val jsonText = Png.read(file.absolutePath)
            val json = JsonParser().parse(jsonText).asJsonObject

            val chartTitle = json["Title"].asString
            val chartDescription = json["Description"].asString

            val converterArray = json["Converters"].asJsonArray
            val dataPointConverters = converterArray.map {
                DataPointConverter.fromJson(it as JsonObject)
            }

            val dataArray = json["Data"].asJsonArray
            val rawData = dataArray.map {
                it as JsonObject
                val time = it["Millis"].asLong
                val reading = it["Reading"].asDouble
                time to reading
            }

            return DataExplorerView(rawData, chartTitle, chartDescription, dataPointConverters)
        }
    }
}