package org.stevenlowes.project.gui.dataexplorer.converters

import com.google.gson.JsonObject

abstract class DataPointConverter: (Pair<Long, Double>?) -> Pair<Long, Double>?{
    abstract fun clear()

    abstract fun toJson(): JsonObject
    companion object {
        fun fromJson(json: JsonObject): DataPointConverter{
            return when(json["Type"].asString){
                AbsConverter::class.simpleName -> AbsConverter()
                GradientConverter::class.simpleName -> GradientConverter()
                MovingMeanConverter::class.simpleName -> MovingMeanConverter(json["Millis"].asLong)
                else -> throw NotImplementedError("Unknown Converter Type")
            }
        }
    }
}