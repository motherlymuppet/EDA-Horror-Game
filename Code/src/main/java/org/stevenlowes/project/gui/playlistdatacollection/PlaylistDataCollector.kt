package org.stevenlowes.project.gui.playlistdatacollection

import com.wrapper.spotify.model_objects.specification.PlaylistTrack
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

                ui {
                    dialog {
                        label {
                            text = "How Relaxing was that Song?"
                        }
                        button {
                            text = "Very Relaxing"
                            action {
                                chart.addLabel("Very Relaxing")
                                if (playlistIdx < tracks.size) {
                                    runAsync {} ui {
                                        workspace.dock(DataExplorerView(chart))
                                    }
                                }
                                close()
                            }
                        }
                        button {
                            text = "Relaxing"
                            action {
                                chart.addLabel("Relaxing")
                                if (playlistIdx < tracks.size) {
                                    runAsync {} ui {
                                        workspace.dock(DataExplorerView(chart))
                                    }
                                }
                                close()
                            }
                        }
                        button {
                            text = "Neutral"
                            action {
                                chart.addLabel("Neutral")
                                if (playlistIdx < tracks.size) {
                                    runAsync {} ui {
                                        workspace.dock(DataExplorerView(chart))
                                    }
                                }
                                close()
                            }
                        }
                        button {
                            text = "Stressful"
                            action {
                                chart.addLabel("Stressful")
                                if (playlistIdx < tracks.size) {
                                    runAsync {} ui {
                                        workspace.dock(DataExplorerView(chart))
                                    }
                                }
                                close()
                            }
                        }
                        button {
                            text = "Very Stressful"
                            action {
                                chart.addLabel("Very Stressful")
                                if (playlistIdx < tracks.size) {
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
