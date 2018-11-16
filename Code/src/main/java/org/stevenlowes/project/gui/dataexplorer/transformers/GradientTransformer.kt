package org.stevenlowes.project.gui.dataexplorer.transformers

import com.google.gson.JsonObject
import org.stevenlowes.project.gui.chart.DataLabel

class GradientTransformer: AbstractTransformer(){
    var last: Pair<Long, Double>? = null

    override fun invoke(labels: List<DataLabel>, pair: Pair<Long, Double>?): Pair<Long, Double>? {
        pair ?: return null

        val prev = last
        last = pair

        if(prev == null){
            return null
        }
        else{
            val delta = pair.second - prev.second
            val seconds = (pair.first - prev.first).toDouble() / 1000
            return pair.first to (delta/seconds)
        }
    }

    override fun clear() {
        last = null
    }

    override fun toJson(): JsonObject {
        val obj = JsonObject()
        obj.addProperty("Type", javaClass.simpleName)
        return obj
    }

    override fun toString() = "Gradient"
}