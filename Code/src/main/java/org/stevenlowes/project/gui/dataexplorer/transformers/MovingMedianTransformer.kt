package org.stevenlowes.project.gui.dataexplorer.transformers

import com.google.gson.JsonObject
import org.stevenlowes.project.gui.chart.DataLabel

class MovingMedianTransformer(private val millis: Long) : AbstractTransformer() {
    private val values = mutableListOf<Pair<Long, Double>>()

    override fun clear() {
        values.clear()
    }

    override fun invoke(labels: List<DataLabel>, pair: Pair<Long, Double>?): Pair<Long, Double>? {
        pair ?: return null

        values.add(pair)

        val showFrom = pair.first - millis

        val deleteTo = values.indexOfFirst { (time, _) -> time > showFrom } - 1
        if(deleteTo != -2){
            (0..deleteTo).forEach { _ ->
                values.removeAt(0)
            }
        }

        if(values.size < 3){
            return null
        }

        val sorted = values.sortedBy { it.first }
        return sorted[sorted.size/2]
    }

    override fun toJson(): JsonObject {
        val obj = JsonObject()
        obj.addProperty("Type", javaClass.simpleName)
        obj.addProperty("Millis", millis)
        return obj
    }

    override fun toString() = "Moving Median (${millis/1000} secs)"
}