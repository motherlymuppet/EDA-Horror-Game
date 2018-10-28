package org.stevenlowes.project.gui.songnearness

import javafx.beans.binding.BooleanExpression
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import org.stevenlowes.project.gui.MainMenu
import org.stevenlowes.project.spotifyAPI.DataContainer
import org.stevenlowes.project.spotifyAPI.Spotify
import org.stevenlowes.project.spotifyAPI.scrapers.ManualUse
import tornadofx.*

class SongNearnessView : View("Song Nearness") {
    private lateinit var dataContainer: DataContainer
    private val foundSongFragment = FoundSongFragment()

    init {
        runAsync { DataContainer() } ui {
            dataContainer = it
            createRealRoot()
        }

        disableSave()
        disableRefresh()
        disableCreate()
        disableDelete()
    }

    private fun consumeId(id: String) {
        runAsync {
            val vector = ManualUse.translate(id)
            dataContainer.nearest(vector, listOf(id))
        } ui { (foundId , distance) ->
            updateFound(foundId, distance)
        }
    }

    private fun updateFound(id: String, distance: Float) {
        val track = Spotify.getAnalysedTrack(id)
        with(foundSongFragment){
            trackProperty.set(track)
            distanceProperty.set(distance)
        }
    }

    override val root = vbox {
        label {
            text = "Please wait for the data container to be initialised"
            alignment = Pos.CENTER
        }
    }

    private fun createRealRoot() {
        with(root){
            clear()

            add(workspace.backButton)

            add(SongSelectionFragment { consumeId(it) })
            add(foundSongFragment)
        }
    }
}