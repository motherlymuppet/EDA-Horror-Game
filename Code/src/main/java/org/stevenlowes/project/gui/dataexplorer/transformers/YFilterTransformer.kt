package org.stevenlowes.project.gui.dataexplorer.transformers

import com.google.gson.JsonObject
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class YFilterTransformer(min: Number?, max: Number?): AbstractTransformer(){
    private val min = min?.toDouble()
    private val max = max?.toDouble()

    override fun invoke(pair: Pair<Long, Double>?): Pair<Long, Double>? {
        pair ?: return null
        if(min != null && min > pair.second){
            return null
        }
        if(max != null && max < pair.second){
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

    override fun toString() = "Y Filter: ${min?.roundToLong()} to ${max?.roundToLong()}"
}