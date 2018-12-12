package org.stevenlowes.project.gui.dataexplorer.transformers

import com.google.gson.JsonObject
import org.stevenlowes.project.gui.chart.DataLabel
import java.lang.Math.max

class MovingMeanTransformer(private val millis: Long) : AbstractTransformer() {
    val values = mutableListOf<Pair<Long, Double>>()

    override fun clear() {
        values.clear()
    }

    override fun invoke(labels: List<DataLabel>, pair: Pair<Long, Double>?): Pair<Long, Double>? {
        pair ?: return null

        values.add(pair)

        val showFrom = pair.first - millis

        val deleteTo = values.indexOfFirst { (time, _) -> time > showFrom } - 1
        if(deleteTo >= 0){
            (0..deleteTo).forEach { _ ->
                values.removeAt(0)
            }
        }

        if (values.isEmpty()) {
            return null
        }

        if (values.size == 1) {
            return values.first()
        }

        val first = values[0]
        val second = values[1]

        val realFirstTime = max(showFrom, first.first)

        val expectedDuration = second.first - first.first
        val realDuration = second.first - realFirstTime
        val durationFraction = realDuration.toDouble() / expectedDuration

        val firstDelta = second.second - first.second
        val realDelta = firstDelta * durationFraction
        val realFirstValue = second.second - realDelta

        values[0] = realFirstTime to realFirstValue

        val duration: Double = values.last().first.toDouble() - max(showFrom, values.first().first)

        val mean = values.zipWithNext { (currTime, currValue), (nextTime, nextValue) ->
            val timeDelta: Double = (nextTime - currTime).toDouble()
            val relevance: Double = timeDelta / duration
            val averageValue = (nextValue + currValue) / 2
            return@zipWithNext relevance * averageValue
        }.sum()

        if(mean == Double.NEGATIVE_INFINITY){
            println("Error")
        }

        return pair.first to mean
    }

    override fun toJson(): JsonObject {
        val obj = JsonObject()
        obj.addProperty("Type", javaClass.simpleName)
        obj.addProperty("Millis", millis)
        return obj
    }

    override fun toString() = "Moving Mean ($millis ms)"
}