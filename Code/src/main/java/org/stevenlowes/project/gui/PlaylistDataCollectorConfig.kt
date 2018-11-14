package org.stevenlowes.project.gui

import com.wrapper.spotify.model_objects.specification.PlaylistSimplified
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Orientation
import javafx.scene.Parent
import javafx.util.StringConverter
import org.stevenlowes.project.gui.playlistdatacollection.PlaylistDataCollector
import org.stevenlowes.project.spotifyAPI.Spotify
import tornadofx.*

class PlaylistDataCollectorConfig: Fragment() {
    private var playlists: List<PlaylistSimplified> = Spotify.getPlaylists()
    private val selectedPlaylist = SimpleObjectProperty<PlaylistSimplified>()
    private val playWholeSong = SimpleBooleanProperty(true)
    private val songPlayTime = SimpleIntegerProperty(60)
    private val restTime = SimpleIntegerProperty(10)
    private var set = false

    override val root = form{
        fieldset("Playlist Data Collector Config") {
            field("Playlist") {
                combobox(selectedPlaylist, playlists){
                    converter = object: StringConverter<PlaylistSimplified>(){
                        override fun toString(obj: PlaylistSimplified?) = obj?.name ?: "None"
                        override fun fromString(string: String?): PlaylistSimplified = throw NotImplementedError()
                    }
                }
            }

            field("Play Whole Song") {
                checkbox("Enable", playWholeSong)
            }

            field("Song Play Time") {
                hbox(4) {
                    slider(1, 300, null, Orientation.HORIZONTAL){
                        bind(songPlayTime)
                    }

                    textfield(songPlayTime)
                    label("Seconds")
                }

                enableWhen(playWholeSong.not())
            }

            field("Rest Time") {
                hbox(4) {
                    slider(0, 300, null, Orientation.HORIZONTAL){
                        bind(restTime)
                    }

                    textfield(restTime)
                    label("Seconds")
                }
            }

            hbox {
                button("Ok") {
                    action{
                        set = true
                        close()
                    }
                }

                button("Cancel"){
                    action{
                        close()
                    }
                }
            }
        }
    }

    fun show(): Boolean {
        set = false
        openModal(block = true)
        if(set){
            PlaylistDataCollector.tracks = Spotify.getPlaylist(selectedPlaylist.get().id)
            PlaylistDataCollector.playTime = if(playWholeSong.get()) null else songPlayTime.get()
            PlaylistDataCollector.restTime = restTime.get()
        }
        return set
    }
}
