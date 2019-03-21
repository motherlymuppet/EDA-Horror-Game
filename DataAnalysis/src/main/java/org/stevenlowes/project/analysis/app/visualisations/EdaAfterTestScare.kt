package org.stevenlowes.project.analysis.app.visualisations

import javafx.scene.paint.Color
import org.stevenlowes.project.analysis.Series
import org.stevenlowes.project.analysis.app.visualisations.datatransforms.transformNormaliseRelative
import org.stevenlowes.project.analysis.app.visualisations.datatransforms.transformSplit
import org.stevenlowes.project.analysis.app.visualisations.datatransforms.transformStartAtMin
import org.stevenlowes.project.analysis.app.visualisations.datatransforms.transformStartAtZero
import org.stevenlowes.project.analysis.data.Playtest
import org.stevenlowes.project.analysis.gui.DataLabel

class EdaAfterTestScare(playtests: List<Playtest>, lengthAfterScareSecs: Double, colorFunc: (Playtest, Long) -> Color) : Visualisation {
    override val series: List<Series> = playtests.flatMap { playtest ->
        playtest.scares.drop(3).map { scare ->
            val lengthAfterScareMs = (lengthAfterScareSecs * 1000).toLong()
            Series(
                "Participant ID ${playtest.participant.id} Test Scare @ $scare",
                colorFunc(playtest, scare),
                playtest.edaData
                    .filter { (time, _) ->
                        time in scare..(scare + lengthAfterScareMs)
                    }
                    .transformStartAtZero(),
                labels = emptyList()
            )
        }
    }

    override val title = "Reaction after Scare (Test)"
    override val xLabel = "Time after scare (ms)"
    override val yLabel = "Eda"
}