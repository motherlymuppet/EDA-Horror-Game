package org.stevenlowes.project.gui.songnearness

import org.stevenlowes.project.spotifyAPI.DataContainer
import org.stevenlowes.project.spotifyAPI.Spotify
import tornadofx.*

class SongNearnessView : View("Song Nearness") {
    private val dataContainer = DataContainer.instance
    private val foundSongFragment = FoundSongFragment()

    init {
        disableSave()
        disableRefresh()
        disableCreate()
        disableDelete()
    }

    private fun consumeId(id: String) {
        runAsync {
            return@runAsync dataContainer.nearest(id, listOf(id))
        } ui { nearest ->
            updateFound(nearest, dataContainer.distance(id, nearest))
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
        add(SongSelectionFragment { consumeId(it) })
        add(foundSongFragment)
    }
}