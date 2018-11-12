package org.stevenlowes.project.gui.dataexplorer.transformers

import com.google.gson.JsonObject

class DestructionTransformer(val keepEvery: Int) : AbstractTransformer() {
    var num = -1

    override fun invoke(pair: Pair<Long, Double>?): Pair<Long, Double>? {
        pair ?: return null

        num = (num + 1) % keepEvery
        return when(num){
            0 -> pair
            else -> null
        }
    }

    override fun clear() {
        num = -1
    }

    override fun toJson(): JsonObject {
        val obj = JsonObject()
        obj.addProperty("Type", javaClass.simpleName)
        obj.addProperty("KeepEvery", keepEvery)
        return obj
    }

    override fun toString() = "Destruction ($keepEvery)"
}