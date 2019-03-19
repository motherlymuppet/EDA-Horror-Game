package org.stevenlowes.project.analysis

import org.stevenlowes.project.analysis.app.visualisations.*
import org.stevenlowes.project.analysis.data.PlaytestLoader
import java.io.File

object Config {
    private const val spreadsheetPath = "E:/Backups/Steven-3rdYrProject/minecraftdata/Spreadsheet.csv"
    private const val participantDataPath = "E:/Backups/Steven-3rdYrProject/minecraftdata/Participant Data"

    private val playests = PlaytestLoader.loadAll(
        File(spreadsheetPath),
        File(participantDataPath)
    )

    private val intervention = playests.filter { it.participant.pair == null }
    private val control = playests.filter { it.participant.pair != null }

    val visualisation: Visualisation =
        //SingleEDA(playests.first { it.participant.id == 10 })
        //AverageEDA(playests)
        //AverageEdaAfterScare(playests, 10.0)
        //EdaAfterScare(intervention, 10.0)
        EdaAfterScare(control, 10.0)


    //ComboVisualisation( AverageEdaAfterScare(intervention, 10.0),AverageEdaAfterScare(control, 11.0))
    //MouseMovement(playests.take(1))
    //MouseMovementAfterScare(playests.take(1), 10.0)
}