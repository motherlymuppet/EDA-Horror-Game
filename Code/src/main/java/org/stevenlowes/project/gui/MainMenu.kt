package org.stevenlowes.project.gui

import javafx.geometry.Pos
import org.stevenlowes.project.gui.datacollection.DataCollectionView
import org.stevenlowes.project.gui.datascreenshot.DataScreenshot
import org.stevenlowes.project.gui.inputmodals.ListInput
import org.stevenlowes.project.gui.playlistdatacollection.PlaylistDataCollector
import org.stevenlowes.project.gui.songnearness.SongNearnessView
import org.stevenlowes.project.serialreader.Serial
import org.stevenlowes.project.spotifyAPI.Spotify
import tornadofx.*

class MainMenu: View("Main Menu"){
    override val root = vbox(spacing = 8, alignment = Pos.CENTER) {
        button("Data Collection") {
            action {
                Serial.withValidPort {
                    workspace.dock<DataCollectionView>()
                }
            }
        }

        button("Playlist Data Collection"){
            action{
                Serial.withValidPort {
                    if(PlaylistDataCollectorConfig().show()){
                        workspace.dock<PlaylistDataCollector>()
                    }
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