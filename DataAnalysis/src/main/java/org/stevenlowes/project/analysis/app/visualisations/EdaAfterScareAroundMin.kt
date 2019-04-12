package org.stevenlowes.project.analysis.app.visualisations

import javafx.scene.paint.Color
import org.stevenlowes.project.analysis.Series
import org.stevenlowes.project.analysis.app.visualisations.datatransforms.transformStartAtZero
import org.stevenlowes.project.analysis.data.Playtest
import org.stevenlowes.project.analysis.plusValueInterpolate

class EdaAfterScareAroundMin(
    playtests: List<Playtest>,
    allowableWaitForTrough: Double,
    lengthAroundTrough: Double,
    colorFunc: ((Playtest, Long) -> Color)? = null
) :
    Visualisation {
    override val series: List<Series> = playtests.flatMap { playtest ->
        val lengthAroundTroughMs = (lengthAroundTrough * 1000).toLong()
        val allowableWaitForTroughMs = (allowableWaitForTrough * 1000).toLong()

        playtest.scares.drop(3).mapNotNull { scare ->
            val data = getData(playtest.edaData, scare, lengthAroundTroughMs, allowableWaitForTroughMs) ?: return@mapNotNull null

            Series(
                "Participant ${playtest.participant.id} eda after scare around min, Scare @ $scare",
                colorFunc?.invoke(playtest, scare),
                data,
                emptyList()
            ).transformStartAtZero()
        }
    }

    override val title = "Reaction around Scare Min (Test)"
    override val xLabel = "Time after min (ms)"
    override val yLabel = "Eda"

    private fun getData(data: Map<Long, Double>, troughScanStart: Long, lengthAroundTroughMs: Long, allowableWaitForTroughMs: Long): Map<Long, Double>?{
        val troughScanEnd = troughScanStart + allowableWaitForTroughMs
        val min = data.asSequence()
            .filter { (time, _) -> time in troughScanStart..troughScanEnd }
            .minBy { it.value }!!

        val start = min.key - lengthAroundTroughMs
        val end = min.key + lengthAroundTroughMs

        val output = data
            .plusValueInterpolate(start)
            .plusValueInterpolate(end)
            .filter { it.key in start..end }

        val actualLength = output.keys.max()!! - output.keys.min()!!

        return if(actualLength < lengthAroundTroughMs * 2){
            null
        } else{
            output
        }
    }
}