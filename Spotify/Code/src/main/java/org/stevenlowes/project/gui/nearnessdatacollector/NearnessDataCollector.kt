package org.stevenlowes.project.gui.nearnessdatacollector

import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import org.stevenlowes.project.gui.chart.AutoLowerBound
import org.stevenlowes.project.gui.datacollection.DataCollectionChart
import org.stevenlowes.project.gui.dataexplorer.DataExplorerView
import org.stevenlowes.project.spotifyAPI.DataContainer
import org.stevenlowes.project.spotifyAPI.Spotify
import tornadofx.*

class NearnessDataCollector : View("Playlist Data Collection") {
    private val chart: DataCollectionChart = DataCollectionChart(AutoLowerBound.FIVE_MINUTES)
    private val currentlyPlaying = SimpleStringProperty("None")
    private val dataContainer = DataContainer.instance

    private var stage = 0
    private lateinit var lastSongId: String

    companion object {
        var playTime: Int? = null
        var restTime: Int = 0
    }

    init {
        chart.labelSize = 12

        whenSaved {
            workspace.dock(DataExplorerView(chart))
        }

        whenDocked {
            chart.start()
            play()
        }

        whenUndocked {
            chart.stop()
            Spotify.pause()
        }

        disableDelete()
        disableCreate()
        disableRefresh()
    }

    private val songs: MutableList<String> = mutableListOf()

    private fun play() {
        if (isDocked) {
            Thread {
                val trackId = when (stage) {
                    0 -> dataContainer.randomId()
                    1 -> dataContainer.nearest(lastSongId, songs)
                    2 -> dataContainer.closestToDistance(10f, lastSongId, songs)
                    3 -> dataContainer.nearest(lastSongId, songs)
                    else -> throw IllegalStateException()
                }

                val track = Spotify.getTrack(trackId)
                val distance = if (stage == 0) {
                    null
                }
                else {
                    dataContainer.distance(trackId, lastSongId)
                }

                stage = (stage + 1) % 4
                lastSongId = trackId
                songs.add(trackId)

                Thread.sleep(restTime * 1000L)

                if (distance == null) {
                    chart.addLabel("Start: ${track.name}")
                }
                else {
                    chart.addLabel("Start ($distance): ${track.name}")
                }

                Platform.runLater {
                    currentlyPlaying.set(track.name)
                }

                val maxLengthMs = if (playTime == null) null else playTime!! * 1000L
                Spotify.play(track.id, maxLengthMs) {
                    chart.addLabel("End")
                    play()
                }
            }.start()
        }
    }

    override val root = vbox {
        add(chart)
        label {
            alignment = Pos.CENTER
            bind(currentlyPlaying)
        }
    }
}
