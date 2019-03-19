package org.stevenlowes.project.analysis.app.visualisations

import org.stevenlowes.project.analysis.gui.DataLabel

class ComboVisualisation(vararg visualisations: Visualisation): Visualisation{
    override val data: List<Map<Long, Double>> = visualisations.flatMap { it.data }
    override val labels: List<DataLabel> = visualisations.flatMap { it.labels }
    override val title: String = "Combo"
    override val xLabel: String = "X"
    override val yLabel: String = "Y"
}