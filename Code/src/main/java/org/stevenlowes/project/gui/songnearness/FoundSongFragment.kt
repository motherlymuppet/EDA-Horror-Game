package org.stevenlowes.project.gui.songnearness

import javafx.beans.property.Property
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleFloatProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.util.StringConverter
import org.stevenlowes.project.spotifyAPI.AnalysedTrack
import org.stevenlowes.project.spotifyAPI.Spotify
import tornadofx.*

class FoundSongFragment: Fragment("Found Song") {
    val trackProperty = SimpleObjectProperty<AnalysedTrack>()
    val distanceProperty = SimpleFloatProperty()

    override val root = vbox(alignment = Pos.CENTER){
        label{
            bind(trackProperty, readonly = true, converter = object : StringConverter<AnalysedTrack>() {
                override fun toString(track: AnalysedTrack?) = track?.name ?: "No Track"
                override fun fromString(string: String?) = throw NotImplementedError("Should never be called")
            })
        }

        label {
            bind(distanceProperty)
        }

        button {
            text = "Play"
            action { Spotify.play(trackProperty.get().id) }
        }
    }
}
