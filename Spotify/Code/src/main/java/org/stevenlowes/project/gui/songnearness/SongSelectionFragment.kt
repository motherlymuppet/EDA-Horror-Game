package org.stevenlowes.project.gui.songnearness

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Orientation
import javafx.scene.Parent
import org.stevenlowes.project.spotifyAPI.Spotify
import tornadofx.*

class SongSelectionFragment(idConsumer: (String) -> Unit): Fragment("Song Selection"){
    override val root = hbox {
        val songId = SimpleStringProperty("")
        textfield(songId)
        button("With Song ID") {
            action {
                idConsumer(songId.get())
            }
        }
        separator(Orientation.VERTICAL)
        button("Currently Playing") {
            action {
                val track = Spotify.currentlyPlaying()
                idConsumer(track.id)
            }
        }
    }
}