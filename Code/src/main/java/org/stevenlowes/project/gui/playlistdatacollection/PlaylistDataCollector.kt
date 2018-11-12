package org.stevenlowes.project.gui.playlistdatacollection

import com.sun.xml.internal.txw2.output.DataWriter
import com.wrapper.spotify.model_objects.specification.PlaylistTrack
import gnu.io.CommPortIdentifier
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
        lateinit var tracks: List<PlaylistTrack>
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

    private val maxLength: Int? = 10

    private fun play(){
        runAsync {
            Thread.sleep(10 * 1000)
            val track = tracks[playlistIdx].track
            chart.addLabel("Start: ${track.name}")

            ui {
                currentlyPlaying.set(track.name)
            }

            val maxLengthMs = if(maxLength == null) null else maxLength * 1000L
            Spotify.play(track.id, maxLengthMs) {
                chart.addLabel("End")
                playlistIdx++

                ui {
                    dialog {
                        label {
                            text = "How Relaxing was that Song?"
                        }
                        button {
                            text = "Very Relaxing"
                            action {
                                chart.addLabel("Very Relaxing")
                                close()
                            }
                        }
                        button {
                            text = "Relaxing"
                            action {
                                chart.addLabel("Relaxing")
                                close()
                            }
                        }
                        button {
                            text = "Neutral"
                            action {
                                chart.addLabel("Neutral")
                                close()
                            }
                        }
                        button {
                            text = "Stressful"
                            action {
                                chart.addLabel("Stressful")
                                close()
                            }
                        }
                        button {
                            text = "Very Stressful"
                            action {
                                chart.addLabel("Very Stressful")
                                close()
                            }
                        }
                    }?.show()
                }

                if(playlistIdx < tracks.size && !chart.paused) {
                    play()
                }
                else{
                    workspace.dock(DataExplorerView(chart))
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
