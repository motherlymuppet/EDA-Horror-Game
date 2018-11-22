package org.stevenlowes.project.gui.dataexplorer.transformers

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.stevenlowes.project.gui.chart.DataLabel

abstract class AbstractTransformer : (List<DataLabel>, Pair<Long, Double>?) -> Pair<Long, Double>? {
    abstract fun clear()

    abstract fun toJson(): JsonObject

    companion object {
        fun fromJson(json: JsonObject): AbstractTransformer {
            return when (json["Type"].asString) {
                AbsTransformer::class.simpleName -> AbsTransformer()
                GradientTransformer::class.simpleName -> GradientTransformer()
                MovingMeanTransformer::class.simpleName -> MovingMeanTransformer(json["Millis"].asLong)
                DestructionTransformer::class.simpleName -> DestructionTransformer(json["KeepEvery"].asInt)
                MovingMedianTransformer::class.simpleName -> MovingMedianTransformer(json["Millis"].asLong)
                XFilterTransformer::class.simpleName -> XFilterTransformer(json.getOrNull("Min")?.nullOr { it.asLong },
                                                                           json.getOrNull("Max")?.nullOr { it.asLong })
                YFilterTransformer::class.simpleName -> YFilterTransformer(json.getOrNull("Min")?.nullOr { it.asDouble },
                                                                           json.getOrNull("Max")?.nullOr { it.asDouble })
                LabelPauseTransformer::class.simpleName -> LabelPauseTransformer()
                else -> throw NotImplementedError("Unknown Converter Type")
            }
        }

        //TODO just implement double(key) and .nullable.double(key)

        private fun JsonObject.getOrNull(key: String) = when {
            has(key) -> get(key)
            else -> null
        }

        private fun <T> JsonElement.nullOr(function: (JsonElement) -> T) = when {
            isJsonNull -> null
            else -> function(this)
        }
    }
}