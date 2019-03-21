package org.stevenlowes.project.analysis.app.visualisations

import org.stevenlowes.project.analysis.Series

class ComboVisualisation(vararg visualisations: Visualisation): Visualisation{
    override val series: List<Series> = visualisations.flatMap { it.series }
    override val title: String = visualisations.first().title
    override val xLabel: String = visualisations.first().xLabel
    override val yLabel: String = visualisations.first().yLabel
}