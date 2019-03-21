package org.stevenlowes.project.analysis.app.visualisations

import org.stevenlowes.project.analysis.Series
import org.stevenlowes.project.analysis.app.visualisations.datatransforms.*
import org.stevenlowes.project.analysis.gui.DataLabel
import org.stevenlowes.project.analysis.wrapWithList

interface Visualisation {
    val series: List<Series>
    val title: String
    val xLabel: String
    val yLabel: String

    val labels: List<DataLabel> get() = series.flatMap { it.labels }

    fun overlap() = generateVis { it.filterOverlap() }
    fun average() = generateVis { it.transformAverage().wrapWithList() }
    fun averageWithError() = generateVis { it.transformAverageWithError() }
    fun median() = generateVis { it.transformMedian().wrapWithList() }
    fun normaliseAbs() = generateVis {it.map { it.transformNormaliseAbsolute() } }
    fun normaliseRel() = generateVis {it.map { it.transformNormaliseRelative() } }

    fun mapSeries(func: (Series) -> Series) = generateVis { it.map(func) }

    private fun generateVis(func: (List<Series>) -> List<Series>) =
        object : Visualisation {
            override val series = func(this@Visualisation.series)
            override val title = this@Visualisation.title
            override val xLabel = this@Visualisation.xLabel
            override val yLabel = this@Visualisation.yLabel
        }

    operator fun plus(other: Visualisation) = ComboVisualisation(this, other)
}