package org.stevenlowes.project.analysis.app.visualisations

import javafx.scene.paint.Color
import org.stevenlowes.project.analysis.Series
import org.stevenlowes.project.analysis.app.visualisations.datatransforms.transformStartAtZero
import org.stevenlowes.project.analysis.data.Playtest
import org.stevenlowes.project.analysis.nullIfNull

class EdaAfterCalibrationScare(
    playtests: List<Playtest>,
    lengthAfterScareSecs: Double,
    colorFunc: ((Playtest, Long) -> Color)? = null
) : Visualisation {
    override val series: List<Series> = playtests.flatMap { playtest ->
        playtest.scares.take(3).map { scare ->
            val lengthAfterScareMs = (lengthAfterScareSecs * 1000).toLong()
            Series(
                "Participant ID ${playtest.participant.id} Calibration Scare @ $scare",
                colorFunc?.invoke(playtest, scare),
                playtest.edaData
                    .filter { (time, _) ->
                        time in scare..(scare + lengthAfterScareMs)
                    },
                labels = emptyList()
            ).transformStartAtZero()
        }
    }

    override val title = "Reaction after Scare (Calibration)"
    override val xLabel = "Time after scare (ms)"
    override val yLabel = "Eda"
}