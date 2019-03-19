package org.stevenlowes.project.analysis.app.visualisations

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException
import org.stevenlowes.project.analysis.app.visualisations.datatransforms.*
import org.stevenlowes.project.analysis.data.Playtest
import org.stevenlowes.project.analysis.getValueInterpolate
import org.stevenlowes.project.analysis.gui.DataLabel
import org.stevenlowes.project.analysis.wrapWithList
import java.lang.IllegalArgumentException

class MouseMovementAfterScare(playtests: List<Playtest>, lengthAfterScareSecs: Double) : Visualisation {
    override val data: List<Map<Long, Double>> = playtests.flatMap { playtest ->
        playtest.mouseData
            .transformAngleDelta()
            //.transformGraident()
            .transformSplit(
                playtest.scares
                    .drop(3)
                    .map {
                        it to (it + lengthAfterScareSecs * 1000).toLong()
                    }
            )
    }
        .map {
            it.transformStartAtZero()
        }
        .transformAverage()
        .transformDestructor(1000)
        .wrapWithList()


    override val labels: List<DataLabel> = emptyList()

    override val title = "Mouse Movement"
    override val xLabel = "Time (ms)"
    override val yLabel = "Mouse"
}