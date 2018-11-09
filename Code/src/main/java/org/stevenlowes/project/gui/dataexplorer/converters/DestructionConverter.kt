package org.stevenlowes.project.gui.dataexplorer.converters

import com.google.gson.JsonObject

class DestructionConverter(val keepEvery: Int) : DataPointConverter() {
    var num = -1

    override fun invoke(pair: Pair<Long, Double>?): Pair<Long, Double>? {
        pair ?: return null

        num = (num + 1) % keepEvery
        return when(num){
            0 -> pair
            else -> null
        }
    }

    override fun clear() {}

    override fun toJson(): JsonObject {
        val obj = JsonObject()
        obj.addProperty("Type", javaClass.simpleName)
        return obj
    }

    override fun toString() = "Destruction ($keepEvery)"
}