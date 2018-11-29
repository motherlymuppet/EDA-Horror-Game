package org.stevenlowes.project.gui.playlistdatacollection

import com.wrapper.spotify.model_objects.specification.PlaylistTrack
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import org.stevenlowes.project.gui.chart.AutoLowerBound
import org.stevenlowes.project.gui.datacollection.DataCollectionChart
import org.stevenlowes.project.gui.dataexplorer.DataExplorerView
import org.stevenlowes.project.spotifyAPI.Spotify
import tornadofx.*

class PlaylistDataCollector : View("Playlist Data Collection") {
    private val chart: DataCollectionChart = DataCollectionChart(AutoLowerBound.FIVE_MINUTES)
    private val currentlyPlaying = SimpleStringProperty("None")
    private var playlistIdx = 0

    companion object {
        var tracks: List<PlaylistTrack> = listOf()
        var playTime: Int? = null
        var restTime: Int = 0
    }

    init {
        whenSaved {
            workspace.dock(DataExplorerView(chart))
        }

        whenDocked {
            chart.start()
            play()
        }

        whenUndocked {
            chart.stop()
            playlistIdx = 0
            Spotify.pause()
        }

        disableDelete()
        disableCreate()
        disableRefresh()
    }

    private val relaxingProp = SimpleIntegerProperty(50)

    private fun play(){
        runAsync {
            val track = tracks[playlistIdx].track

            Thread.sleep(restTime * 1000L)

            chart.addLabel("Start: ${track.name}")

            ui {
                currentlyPlaying.set(track.name)
            }

            val maxLengthMs = if(playTime == null) null else playTime!! * 1000L
            Spotify.play(track.id, maxLengthMs) {
                chart.addLabel("End")
                playlistIdx++

                val done = playlistIdx == tracks.size - 1

                ui {
                    dialog {
                        label {
                            text = "How Relaxing was that Song?"
                        }
                        slider(0..100){
                            bind(relaxingProp)
                        }
                        button {
                            text = "Submit"
                            action {
                                chart.addLabel("${relaxingProp.get()}%")
                                if (done) {
                                    runAsync {} ui {
                                        workspace.dock(DataExplorerView(chart))
                                    }
                                }
                                close()
                            }
                        }
                    }?.show()
                }

                if(playlistIdx < tracks.size){
                    play()
                }
            }
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
