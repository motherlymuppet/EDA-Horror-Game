package org.stevenlowes.project.gui.dataexplorer.converters

import com.google.gson.JsonObject
import java.lang.Math.max

class MovingAverageConverter(private val millis: Long) : DataPointConverter() {
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


        val sum = values.zipWithNext { (currTime, currValue), (nextTime, nextValue) ->
            val timeDelta = nextTime - currTime
            val averageValue = (nextValue + currValue) / 2
            return@zipWithNext timeDelta * averageValue
        }.sum()

        val duration = values.last().first - max(showFrom, values.first().first)
        val average = sum / duration

        if(average == Double.NEGATIVE_INFINITY){
            println("Error")
        }

        return pair.first to average
    }

    override fun toJson(): JsonObject {
        val obj = JsonObject()
        obj.addProperty("Type", javaClass.simpleName)
        obj.addProperty("Millis", millis)
        return obj
    }

    override fun toString() = "Moving Average ($millis ms)"
}