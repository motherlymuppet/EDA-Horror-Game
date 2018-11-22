package org.stevenlowes.project.gui.dataexplorer.transformers

import com.google.gson.JsonObject
import org.stevenlowes.project.gui.chart.DataLabel

class LabelPauseTransformer : AbstractTransformer() {
    override fun invoke(labels: List<DataLabel>, pair: Pair<Long, Double>?): Pair<Long, Double>? {
        pair ?: return null

        val validLabels = labels.filter { it.x.toLong() <= pair.first }

        val mostRecentPause = validLabels.filter { it.text.startsWith("End") }.maxBy { it.x.toLong() }
                ?: return pair

        validLabels.filter { it.x.toLong() > mostRecentPause.x.toLong() && it.text.startsWith("Start") }.minBy { it.x.toLong() }
                ?: return pair.first to 0.0

        return pair
    }

    override fun clear() {}

    override fun toJson(): JsonObject {
        val obj = JsonObject()
        obj.addProperty("Type", javaClass.simpleName)
        return obj
    }

    override fun toString() = "Label Pause Filter"
}