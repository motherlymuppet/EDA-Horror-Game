package org.stevenlowes.project.gui.dataexplorer.converters

import com.google.gson.JsonObject
import java.time.Duration
import java.time.LocalDateTime

class GradientConverter: DataPointConverter(){
    var last: Pair<Long, Double>? = null

    override fun invoke(pair: Pair<Long, Double>?): Pair<Long, Double>? {
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