package org.stevenlowes.project.analysis.app.visualisations

import javafx.scene.paint.Color
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
    fun average(newName: String? = null, color: Color? = null) =
        generateVis { it.transformAverage(newName, color).wrapWithList() }

    fun averageWithError(newName: String? = null, color: Color? = null, errorColor: Color? = null) =
        generateVis { it.transformAverageWithError(newName, color, errorColor) }

    fun median(newName: String? = null, color: Color? = null) =
        generateVis { it.transformMedian(newName, color).wrapWithList() }

    fun normaliseAbs() = generateVis { it.transformNormaliseAbsolute() }
    fun normaliseRel() = generateVis { it.transformNormaliseRelative() }

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