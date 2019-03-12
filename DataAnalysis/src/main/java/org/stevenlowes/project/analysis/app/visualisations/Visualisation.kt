package org.stevenlowes.project.analysis.app.visualisations

import org.stevenlowes.project.analysis.data.PlayTest
import org.stevenlowes.project.analysis.gui.DataLabel

interface Visualisation {
    val data: List<Map<Long, Double>>
    val labels: List<DataLabel>

    val title: String
    val xLabel: String
    val yLabel: String
}
