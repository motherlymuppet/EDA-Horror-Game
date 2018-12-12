package org.stevenlowes.project.gui.dataexplorer.transformers

import com.google.gson.JsonObject
import org.stevenlowes.project.gui.chart.DataLabel
import kotlin.math.abs

class AbsTransformer: AbstractTransformer(){
    override fun invoke(labels: List<DataLabel>, pair: Pair<Long, Double>?): Pair<Long, Double>? {
        pair ?: return null
        val value = pair.second
        val absValue = abs(value)
        return pair.first to absValue
    }

    override fun clear() {}

    override fun toJson(): JsonObject {
        val obj = JsonObject()
        obj.addProperty("Type", javaClass.simpleName)
        return obj
    }

    override fun toString() = "Absolute"
}