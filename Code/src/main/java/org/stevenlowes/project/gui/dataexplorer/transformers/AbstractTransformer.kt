package org.stevenlowes.project.gui.dataexplorer.transformers

import com.google.gson.JsonObject

abstract class AbstractTransformer: (Pair<Long, Double>?) -> Pair<Long, Double>?{
    abstract fun clear()

    abstract fun toJson(): JsonObject
    companion object {
        fun fromJson(json: JsonObject): AbstractTransformer{
            return when(json["Type"].asString){
                AbsTransformer::class.simpleName -> AbsTransformer()
                GradientTransformer::class.simpleName -> GradientTransformer()
                MovingMeanTransformer::class.simpleName -> MovingMeanTransformer(json["Millis"].asLong)
                DestructionTransformer::class.simpleName -> DestructionTransformer(json["KeepEvery"].asInt)
                MovingMedianTransformer::class.simpleName -> MovingMedianTransformer(json["Millis"].asLong)
                XFilterTransformer::class.simpleName -> XFilterTransformer(json["Min"].asLong, json["Max"].asLong)
                YFilterTransformer::class.simpleName -> YFilterTransformer(json["Min"].asDouble, json["Max"].asDouble)
                else -> throw NotImplementedError("Unknown Converter Type")
            }
        }
    }
}