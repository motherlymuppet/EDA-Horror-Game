package org.stevenlowes.project.analysis.app.visualisations

import org.stevenlowes.project.analysis.data.PlayTest
import org.stevenlowes.project.analysis.gui.DataLabel

class SingleEDA(playtest: PlayTest): Visualisation{
    private val series = playtest.edaData.mapValues { it.value.toDouble() }

    override val data: List<Map<Long, Double>> = listOf(series)

    override val labels: List<DataLabel> =
            playtest.scares.map { x -> DataLabel("Scare", x, yFor(x)) }
                .plus(DataLabel("Start", 0, yFor(0)))
                .plus(DataLabel("End", playtest.lengthMs, yFor(playtest.lengthMs)))

    override val title: String = "Participant ${playtest.participant.id} EDA"
    override val xLabel: String = "Time (ms)"
    override val yLabel: String = "EDA"

    private fun yFor(x: Long): Double{
        val xKey = series.keys.filter { it > x }.min()!!
        return series.getValue(xKey)
    }
}