package org.stevenlowes.project.gui

import javafx.geometry.Pos
import org.stevenlowes.project.gui.datacollection.DataCollectionView
import org.stevenlowes.project.gui.datascreenshot.DataScreenshot
import org.stevenlowes.project.gui.songnearness.SongNearnessView
import org.stevenlowes.project.gui.util.PortSelector
import tornadofx.*

class MainMenu: View("Main Menu"){
    override val root = vbox(spacing = 8, alignment = Pos.CENTER) {
        button("Data Collection") {
            action {
                if(DataCollectionView.port == null){
                    DataCollectionView.port = PortSelector.getPort()
                }

                if(DataCollectionView.port != null){
                    workspace.dock<DataCollectionView>()
                }
            }
        }

        button("Load Saved Data") {
            action {
                val dataExplorer = DataScreenshot.exploreScreenshot()
                if(dataExplorer != null){
                    workspace.dock(dataExplorer)
                }
            }
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