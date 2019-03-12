package org.stevenlowes.project.analysis.app

import org.stevenlowes.project.analysis.Config
import org.stevenlowes.project.analysis.gui.LabelledLineChart
import tornadofx.View

class MainMenu : View("Chart") {
    override val root = LabelledLineChart(Config.visualisation)
}