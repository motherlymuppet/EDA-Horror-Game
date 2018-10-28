package org.stevenlowes.project.gui

import javafx.geometry.Pos
import org.stevenlowes.project.gui.datacollection.DataCollectionView
import org.stevenlowes.project.gui.songnearness.SongNearnessView
import tornadofx.*

class MainMenu: View("Main Menu"){
    override val root = vbox(spacing = 8, alignment = Pos.CENTER) {
        button("Data Collection") {
            action { workspace.dock<DataCollectionView>()}
        }

        button("Song Nearness") {
            action { workspace.dock<SongNearnessView>() }
        }
    }

    init {
        disableSave()
        disableCreate()
        disableDelete()
        disableRefresh()
    }
}