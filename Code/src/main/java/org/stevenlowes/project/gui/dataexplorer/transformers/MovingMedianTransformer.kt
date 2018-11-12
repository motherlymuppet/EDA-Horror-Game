package org.stevenlowes.project.gui.dataexplorer.transformers

import com.google.gson.JsonObject

class MovingMedianTransformer(private val millis: Long) : AbstractTransformer() {
    val values = mutableListOf<Pair<Long, Double>>()

    override fun clear() {
        values.clear()
    }

    override fun invoke(pair: Pair<Long, Double>?): Pair<Long, Double>? {
        pair ?: return null

        if(pair.second == Double.NEGATIVE_INFINITY || pair.second == Double.POSITIVE_INFINITY){
            println("Error")
        }

        values.add(pair)

        if(values.size < 3){
            return null
        }

        val showFrom = pair.first - millis

        val deleteTo = values.indexOfFirst { (time, _) -> time > showFrom } - 1
        if(deleteTo != -2){
            (0..deleteTo).forEach { _ ->
                values.removeAt(0)
            }
        }

        val sorted = values.sortedBy { it.first }
        val median = sorted[sorted.size/2]

        return median
    }

    override fun toJson(): JsonObject {
        val obj = JsonObject()
        obj.addProperty("Type", javaClass.simpleName)
        obj.addProperty("Millis", millis)
        return obj
    }

    override fun toString() = "Moving Median ($millis ms)"
}