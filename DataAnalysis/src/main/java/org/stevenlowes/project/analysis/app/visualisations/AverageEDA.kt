package org.stevenlowes.project.analysis.app.visualisations

import org.stevenlowes.project.analysis.app.visualisations.datatransforms.transformAverage
import org.stevenlowes.project.analysis.app.visualisations.datatransforms.transformNormalise
import org.stevenlowes.project.analysis.app.visualisations.datatransforms.filterOverlap
import org.stevenlowes.project.analysis.app.visualisations.datatransforms.transformDestructor
import org.stevenlowes.project.analysis.data.Playtest
import org.stevenlowes.project.analysis.getValueInterpolate
import org.stevenlowes.project.analysis.gui.DataLabel
import org.stevenlowes.project.analysis.wrapWithList

class AverageEDA(playtests: List<Playtest>) : Visualisation {
    private val averaged: Map<Long, Double> =
        playtests.map { playtest -> playtest.edaData }
            .filterOverlap()
            .map { it.transformNormalise() }
            .transformAverage()
            .transformDestructor(1000)

    override val data = averaged.wrapWithList()

    override val labels: List<DataLabel> =
        DataLabel(
            "Start",
            0,
            averaged.getValueInterpolate(0L)!!
        ).wrapWithList()

    override val title: String = "Participant Average EDA"
    override val xLabel: String = "Time (ms)"
    override val yLabel: String = "EDA"
}