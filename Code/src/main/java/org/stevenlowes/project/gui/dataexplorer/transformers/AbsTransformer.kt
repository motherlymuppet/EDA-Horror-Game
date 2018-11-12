package org.stevenlowes.project.gui.dataexplorer.transformers

import com.google.gson.JsonObject
import kotlin.math.abs

class AbsTransformer: AbstractTransformer(){
    override fun invoke(pair: Pair<Long, Double>?): Pair<Long, Double>? {
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