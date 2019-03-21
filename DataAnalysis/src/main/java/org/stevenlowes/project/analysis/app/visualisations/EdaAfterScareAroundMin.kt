package org.stevenlowes.project.analysis.app.visualisations

import javafx.scene.paint.Color
import org.stevenlowes.project.analysis.Series
import org.stevenlowes.project.analysis.app.visualisations.datatransforms.transformStartAtZero
import org.stevenlowes.project.analysis.data.Playtest
import org.stevenlowes.project.analysis.gui.DataLabel

class EdaAfterScareAroundMin(
    playtests: List<Playtest>,
    allowableWaitForTrough: Double,
    lengthAroundTrough: Double,
    colorFunc: (Playtest, Long) -> Color
) :
    Visualisation {
    override val series: List<Series> = playtests.flatMap { playtest ->
        val data = playtest.edaData
        val lengthAroundTroughMs = (lengthAroundTrough * 1000).toLong()
        val allowableWaitForTroughMs = (allowableWaitForTrough * 1000).toLong()

        playtest.scares.drop(3).mapNotNull { scare ->
            Series(
                "Participant ${playtest.participant.id} eda after scare around min, Scare @ $scare",
                colorFunc(playtest, scare),
                scare.run {
                    val troughScanStart = this
                    val troughScanEnd = troughScanStart + allowableWaitForTroughMs
                    val min = data.asSequence()
                        .filter { (time, _) -> time in troughScanStart..troughScanEnd }
                        .minBy { it.value }!!

                    val start = min.key - lengthAroundTroughMs
                    val end = min.key + lengthAroundTroughMs

                    if (end > playtest.lengthMs) {
                        return@mapNotNull null
                    } else {
                        return@run playtest.edaData.filter { it.key in start..end }.transformStartAtZero()
                    }
                },
                emptyList()
            )
        }
    }

    override val title = "Reaction around Scare Min (Test)"
    override val xLabel = "Time after min (ms)"
    override val yLabel = "Eda"
}