package org.stevenlowes.project.analysis.app.visualisations

import org.stevenlowes.project.analysis.app.visualisations.datatransforms.*
import org.stevenlowes.project.analysis.data.Playtest
import org.stevenlowes.project.analysis.gui.DataLabel
import org.stevenlowes.project.analysis.wrapWithList

class AverageEdaAfterScare(playtests: List<Playtest>, lengthAfterScareSecs: Double) : Visualisation {
    override val data: List<Map<Long, Double>> = playtests.flatMap { playtest ->
        playtest.edaData.transformSplit(
            playtest.scares
                .drop(3)
                .map { it + 1500 to it + (lengthAfterScareSecs * 1000).toLong() }
        )
    }.map {
        it.transformStartAtZero()
            .transformNormalise()
    }
        .transformAverage()
        .transformDestructor(1000)
        .wrapWithList()


    override val labels: List<DataLabel> = emptyList()

    override val title = "Reaction after Scare"
    override val xLabel = "Time after scare (ms)"
    override val yLabel = "EDA"
}