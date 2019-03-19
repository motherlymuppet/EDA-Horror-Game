package org.stevenlowes.project.analysis.app.visualisations.datatransforms

import org.stevenlowes.project.analysis.data.Angle
import org.stevenlowes.project.analysis.min
import org.stevenlowes.project.analysis.square
import java.lang.Math.abs
import java.lang.Math.sqrt

fun Map<Long, Angle>.transformAngleDelta(): Map<Long, Double>{
    val list = toList().sortedBy { it.first }
    return (0 until (size-1)).map {index ->
        val (x, ang1) = list[index]
        val (_, ang2) = list[index + 1]

        val p1 = ang1.pitch % 360
        val p2 = ang2.pitch % 360
        val y1 = ang1.yaw % 360
        val y2 = ang2.yaw % 360

        val pitchDelta = abs (p2 - p1)
        val yawDelta = abs (y2 - y1)

        val adjPitchDelta = min(pitchDelta, 360-pitchDelta)!!
        val adjYawDelta = min(yawDelta, 360-yawDelta)!!

        if(adjPitchDelta > 150 || adjYawDelta > 150 || adjPitchDelta < 0 || adjYawDelta < 0){
            println("here")
        }

        val delta = sqrt(adjPitchDelta.square() + adjYawDelta.square())
        return@map x to delta
    }.toMap()
}

