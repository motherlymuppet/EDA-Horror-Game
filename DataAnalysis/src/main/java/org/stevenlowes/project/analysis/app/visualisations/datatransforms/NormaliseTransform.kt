package org.stevenlowes.project.analysis.app.visualisations.datatransforms

import org.stevenlowes.project.analysis.getValueInterpolate
import org.stevenlowes.project.analysis.interpolate
import org.stevenlowes.project.analysis.times

fun Map<Long, Double>.transformNormalise(): Map<Long, Double>{
    val multiplicand = 1.0 / getValueInterpolate(0L)!!
    return this * multiplicand
}
