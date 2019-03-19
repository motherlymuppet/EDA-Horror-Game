package org.stevenlowes.project.analysis.app.visualisations

import org.stevenlowes.project.analysis.app.visualisations.datatransforms.transformAngleDelta
import org.stevenlowes.project.analysis.app.visualisations.datatransforms.transformDestructor
import org.stevenlowes.project.analysis.app.visualisations.datatransforms.transformGraident
import org.stevenlowes.project.analysis.data.Playtest
import org.stevenlowes.project.analysis.getValueInterpolate
import org.stevenlowes.project.analysis.gui.DataLabel

class MouseMovement(playtests: List<Playtest>) : Visualisation {
    override val data: List<Map<Long, Double>> = playtests.map {
        it.mouseData
            .transformAngleDelta()
            .transformGraident()
            .transformDestructor(5000)
    }


    override val labels: List<DataLabel> = playtests.zip(data).flatMap { (playtest, series) ->
        playtest.scares.map { scareTime ->
            val scareY = series.getValueInterpolate(scareTime)!!
            DataLabel("Scare", scareTime, scareY)
        }
    }

    override val title = "Mouse Movement"
    override val xLabel = "Time (ms)"
    override val yLabel = "Mouse"
}