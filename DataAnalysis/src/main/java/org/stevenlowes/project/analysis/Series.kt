package org.stevenlowes.project.analysis

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.stream.JsonWriter
import javafx.application.Platform
import javafx.scene.chart.XYChart
import javafx.scene.paint.Color
import jdk.nashorn.internal.ir.annotations.Ignore
import org.stevenlowes.project.analysis.gui.DataLabel
import tornadofx.observable
import java.io.FileWriter

class Series(val name: String,
             @Transient val color: Color?,
             val data: Map<Long, Double>,
             val labels: List<DataLabel>) {
    val xySeries: XYChart.Series<Number, Number> by lazy {
        val series = XYChart.Series<Number, Number>(
            destructedData.map { (x, y) ->
                XYChart.Data<Number, Number>(x, y)
            }.observable()
        )
        series.name = name
        return@lazy series
    }

    fun updateColor(){
        if(color != null) {
            Platform.runLater {
                xySeries.node.style = "-fx-stroke: ${color.hex};"
            }
        }
    }

    val destructedData: Map<Long, Double> by lazy {
        val keepEvery = kotlin.math.max(data.size / 1000, 1)
        val keepKeys =
            data.keys.asSequence().sorted().withIndex().filter { it.index % keepEvery == 0 }.map { it.value }.toSet()
        return@lazy data.filterKeys { it in keepKeys }
    }

    fun withName(name: String) = Series(name, color, data, labels)
    fun withColor(color: Color?) = Series(name, color, data, labels)
    fun withData(data: Map<Long, Double>) = Series(name, color, data, labels)
    fun withLabels(labels: List<DataLabel>) = Series(name, color, data, labels)

    fun writeJson(){
        val array = JsonArray()
        data.map { (time, value) ->
            val obj = JsonObject()
            obj.addProperty("time", time)
            obj.addProperty("value", value)
            return@map obj
        }.forEach {obj ->
            array.add(obj)
        }

        FileWriter(name).use {
            it.write(array.toString())
        }
    }
}
