package org.stevenlowes.project.analysis.app.visualisations.datatransforms

import org.stevenlowes.project.analysis.Series
import org.stevenlowes.project.analysis.plusValueInterpolate

fun Map<Long, Double>.transformSplit(areas: List<LongRange>) =
    areas.map { range ->
        this
            .plusValueInterpolate(range.start)
            .plusValueInterpolate(range.endInclusive)
            .filterKeys { key -> key in range.start..range.endInclusive}
    }