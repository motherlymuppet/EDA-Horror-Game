package org.stevenlowes.project.analysis

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.stream.JsonWriter
import javafx.scene.chart.XYChart
import javafx.scene.paint.Color
import org.stevenlowes.project.analysis.gui.DataLabel
import tornadofx.observable
import java.io.FileWriter

data class Series(val name: String, val color: Color?, val data: Map<Long, Double>, val labels: List<DataLabel>) {
    fun toXYSeries(): XYChart.Series<Number, Number> =
        XYChart.Series<Number, Number>(
            data.map { (x, y) ->
                XYChart.Data<Number, Number>(x, y)
            }.observable()
        )

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
