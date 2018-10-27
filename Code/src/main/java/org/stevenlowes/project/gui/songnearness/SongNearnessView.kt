package org.stevenlowes.project.gui.songnearness

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

    override fun onDock() {
        currentWindow?.sizeToScene()
    }

    override val root = borderpane {
        center = label {
            text = "Please wait for the data container to be initialised"
            alignment = Pos.CENTER
        }
        bottom = button {
            text = "Back"
            action { replaceWith(MainMenu::class) }
        }
    }

    private fun createRealRoot() {
        with(root){
            clear()
            top = SongSelectionFragment { consumeId(it) }.root
            center = foundSongFragment.root
            currentWindow?.sizeToScene()
        }
    }
}