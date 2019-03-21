package org.stevenlowes.project.analysis.app.visualisations.datatransforms

import org.stevenlowes.project.analysis.Series
import org.stevenlowes.project.analysis.getValueInterpolate
import org.stevenlowes.project.analysis.interpolate
import org.stevenlowes.project.analysis.times

fun Map<Long, Double>.transformNormaliseAbsolute(): Map<Long, Double>{
    return this.mapValues { it.value - getValueInterpolate(0L)!! }
}
