package org.stevenlowes.project.analysis.app.visualisations

import javafx.scene.paint.Color
import org.stevenlowes.project.analysis.Series
import org.stevenlowes.project.analysis.app.visualisations.datatransforms.transformStartAtZero
import org.stevenlowes.project.analysis.data.Playtest
import org.stevenlowes.project.analysis.nullIfNull

class EdaAfterAllScare(playtests: List<Playtest>, lengthAfterScareSecs: Double, colorFunc: ((Playtest, Long) -> Color)? = null) : Visualisation {
    override val series: List<Series> = playtests.flatMap { playtest ->
        playtest.scares.map { scare ->
            val lengthAfterScareMs = (lengthAfterScareSecs * 1000).toLong()
            Series(
                "Participant ID ${playtest.participant.id} Scare @ $scare",
                colorFunc?.invoke(playtest, scare),
                playtest.edaData
                    .filter { (time, _) ->
                        time in scare..(scare + lengthAfterScareMs)
                    },
                emptyList()
            ).transformStartAtZero()
        }
    }

    override val title = "Reaction after Scare"
    override val xLabel = "Time after scare (ms)"
    override val yLabel = "Eda"
}