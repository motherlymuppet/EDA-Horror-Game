package org.stevenlowes.project.analysis.app.visualisations.datatransforms

import org.stevenlowes.project.analysis.plusValueInterpolate

fun Map<Long, Double>.transformSplit(areas: List<Pair<Long, Long>>) =
    areas.map { (start, stop) ->
        this
            .plusValueInterpolate(start)
            .plusValueInterpolate(stop)
            .filterKeys { key -> key in start..stop }
    }