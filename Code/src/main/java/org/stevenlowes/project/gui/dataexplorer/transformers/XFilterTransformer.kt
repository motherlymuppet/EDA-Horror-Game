package org.stevenlowes.project.gui.dataexplorer.transformers

import com.google.gson.JsonObject
import org.stevenlowes.project.gui.chart.GsrChart
import java.time.format.DateTimeFormatter

class XFilterTransformer(min: Number?, max: Number?): AbstractTransformer(){
    private val min = min?.toLong()
    private val max = max?.toLong()
    private val minTime = if(min == null) null else GsrChart.timeConverter.toTime(min)
    private val maxTime = if(min == null) null else GsrChart.timeConverter.toTime(min)
    private val formatter = DateTimeFormatter.ofPattern("EEE HH:mm:ss")


    override fun invoke(pair: Pair<Long, Double>?): Pair<Long, Double>? {
        pair ?: return null
        if(min != null && min > pair.first){
            return null
        }
        if(max != null && max < pair.first){
            return null
        }
        return pair
    }

    override fun clear() {}

    override fun toJson(): JsonObject {
        val obj = JsonObject()
        obj.addProperty("Type", javaClass.simpleName)
        obj.addProperty("Min", min)
        obj.addProperty("Max", max)
        return obj
    }

    override fun toString() = "X Filter: ${minTime?.format(formatter)} to ${maxTime?.format(formatter)}"
}