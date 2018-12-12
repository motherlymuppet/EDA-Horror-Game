package org.stevenlowes.project.gui.datacollection

import gnu.io.CommPortIdentifier
import org.stevenlowes.project.gui.dataexplorer.DataExplorerView
import org.stevenlowes.project.gui.inputmodals.TextInput
import tornadofx.*

class DataCollectionView : View("Data Collection") {
    private val chart: DataCollectionChart = DataCollectionChart()

    init {
        whenDeleted {
            chart.clear()
        }

        whenSaved {
            workspace.dock(DataExplorerView(chart))
        }

        whenCreated {
            val last = chart.series.lastOrNull() ?: return@whenCreated
            val labelText = TextInput("Enter Label").getInputBlankIsNull() ?: return@whenCreated
            val x = last.xValue
            chart.addLabel(labelText, x)
        }

        whenDocked {
            chart.start()
        }

        whenUndocked {
            chart.stop()
        }

        disableRefresh()
    }

    override val root = chart
}
