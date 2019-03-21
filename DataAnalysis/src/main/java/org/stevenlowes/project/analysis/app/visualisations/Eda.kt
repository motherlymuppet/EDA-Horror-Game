package org.stevenlowes.project.analysis.app.visualisations

import javafx.scene.paint.Color
import org.stevenlowes.project.analysis.Series
import org.stevenlowes.project.analysis.data.Playtest
import org.stevenlowes.project.analysis.firstValueAfter
import org.stevenlowes.project.analysis.gui.DataLabel

class Eda(playtests: List<Playtest>, colorFunc: (Playtest) -> Color) : Visualisation {
    override val series: List<Series> = playtests.map { playtest ->
        Series(
            "Participant ${playtest.participant.id} EDA",
            colorFunc(playtest),
            playtest.edaData,
            playtest.edaData.run {
                playtest.scares.map { x -> DataLabel("Scare", x, firstValueAfter(x)) }
                    .plus(DataLabel("Start", 0, firstValueAfter(0L)))
                    .plus(DataLabel("End", playtest.lengthMs, firstValueAfter(playtest.lengthMs)))
            }
        )
    }

    override val title: String = "Eda Data"
    override val xLabel: String = "Time (ms)"
    override val yLabel: String = "Eda"
}