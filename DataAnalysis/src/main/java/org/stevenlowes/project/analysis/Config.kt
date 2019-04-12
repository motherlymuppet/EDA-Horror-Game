package org.stevenlowes.project.analysis

import javafx.scene.paint.Color
import org.stevenlowes.project.analysis.app.visualisations.*
import org.stevenlowes.project.analysis.app.visualisations.datatransforms.transformNormaliseAbsolute
import org.stevenlowes.project.analysis.app.visualisations.datatransforms.transformNormaliseRelative
import org.stevenlowes.project.analysis.data.PlaytestLoader
import org.stevenlowes.project.analysis.data.Storyteller
import java.io.File

object Config {
    private const val spreadsheetPath = "E:/Backups/Steven-3rdYrProject/minecraftdata/Spreadsheet.csv"
    private const val participantDataPath = "E:/Backups/Steven-3rdYrProject/minecraftdata/Participant Data"

    //private const val spreadsheetPath = "/home/steven/repos/Steven-3rdYrProject/minecraftdata/Spreadsheet.csv"
    //private const val participantDataPath = "/home/steven/repos/Steven-3rdYrProject/minecraftdata/Participant Data"

    const val showLegend = true

    val playtests = PlaytestLoader.loadAll(
        File(spreadsheetPath),
        File(participantDataPath)
    )

    private val intervention = playtests.filter { it.participant.pair == null }
    private val control = playtests.filter { it.participant.pair != null }

    val visualisation: Visualisation =
        EdaAfterScareAroundMin(intervention, 10.0, 5.0) {_,_ -> Color.RED}.normaliseAbs().averageWithError() +
        EdaAfterScareAroundMin(control, 10.0, 5.0) {_,_ -> Color.BLUE}.normaliseAbs().averageWithError()
}