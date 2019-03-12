package org.stevenlowes.project.analysis

import org.stevenlowes.project.analysis.app.visualisations.SingleEDA
import org.stevenlowes.project.analysis.app.visualisations.Visualisation
import org.stevenlowes.project.analysis.data.PlayTestLoader
import java.io.File

object Config {
    private val spreadsheetPath = "E:/Backups/Steven-3rdYrProject/minecraftdata/Spreadsheet.csv"
    private val participantDataPath = "E:/Backups/Steven-3rdYrProject/minecraftdata/Participant Data"

    private val playTests = PlayTestLoader.loadAll(
        File(spreadsheetPath),
        File(participantDataPath)
    )

    val visualisation: Visualisation = SingleEDA(playTests.first { it.participant.id == 7 })

}