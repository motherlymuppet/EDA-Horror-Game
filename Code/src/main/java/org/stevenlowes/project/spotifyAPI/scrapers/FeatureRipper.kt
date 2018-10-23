package org.stevenlowes.project.spotifyAPI.scrapers

import com.wrapper.spotify.model_objects.specification.AudioFeatures
import org.stevenlowes.project.spotifyAPI.Spotify
import org.stevenlowes.project.spotifyAPI.SpotifyAuth
import java.io.BufferedReader
import java.io.FileReader
import java.io.FileWriter
import java.util.*
import java.util.concurrent.ExecutionException
import kotlin.coroutines.experimental.buildSequence

class FeatureRipper {
    companion object {
        fun runRipper() {
            BufferedReader(FileReader("data.txt")).use { idFile ->
                FileWriter("featuredData.txt").use { fileWriter ->
                    val fileIdSequence = buildSequence {
                        while (true) {
                            val line = idFile.readLine() ?: break
                            yield(line)
                        }
                    }

                    val retryIds: LinkedList<String> = LinkedList()

                    val limit = 100
                    val idPageSequence = buildSequence {
                        var done = false
                        while (!done) {
                            val ids = mutableListOf<String>()
                            while (ids.size < limit) {
                                if (retryIds.isNotEmpty()) {
                                    if (retryIds.size > 100) {
                                        ids.addAll(retryIds.take(100))
                                    }
                                    else {
                                        ids.addAll(retryIds)
                                    }
                                    retryIds.removeAll(ids)
                                }
                                else {
                                    val id = fileIdSequence.firstOrNull()
                                    if (id == null) {
                                        done = true
                                        break
                                    }
                                    ids.add(id)
                                }
                            }
                            yield(ids.toList())
                        }
                    }

                    var doneCount = 0;


                    val simulataneousPages = 100

                    var done = false

                    while (!done) {
                        SpotifyAuth.manualAuth("AQAOuwxSwwmBLmFFYOx4VLURaes8e2lRdscJWLKnkvT8A5lkM95YVzIKFWayqIylXmr7m3iqdx_ND1aruS41U7wEH33S8wLSUElHllT16Ot8s_P_63WaivO9TgEybcizN3F9fg")
                        val futures = (1..simulataneousPages).mapNotNull {
                            //For each page
                            val ids = idPageSequence.firstOrNull()
                            if (ids == null) {
                                return@mapNotNull null
                            }
                            else {
                                if (ids.size != limit) {
                                    done = true
                                }

                                doneCount += ids.size
                                println(doneCount)

                                val idString = ids.joinToString(",")
                                return@mapNotNull ids to Spotify.api.getAudioFeaturesForSeveralTracks(idString).build().executeAsync<Array<AudioFeatures>>()
                            }
                        }

                        futures.asSequence().mapNotNull { (ids, future) ->
                            try {
                                future.get().toList()
                            }
                            catch (e: ExecutionException) {
                                retryIds.addAll(ids)
                                return@mapNotNull null
                            }
                        }
                                .flatten()
                                .mapNotNull { getFeatureString(it) }
                                .forEach(fileWriter::write)

                        Thread.sleep(5000)
                    }
                }
            }
        }


        fun getFeatureString(features: AudioFeatures?): String? {
            features ?: return null
            val id = features.id ?: return null
            val danceability = features.danceability ?: return null
            val energy = features.energy ?: return null
            val key = features.key ?: return null
            val loudness = features.loudness ?: return null
            val mode = features.mode?.mode ?: return null
            val speechiness = features.speechiness ?: return null
            val acousticness = features.acousticness ?: return null
            val instrumentalness = features.instrumentalness ?: return null
            val liveness = features.liveness ?: return null
            val valence = features.valence ?: return null
            val tempo = features.tempo ?: return null
            val durationMs = features.durationMs ?: return null
            val timeSignature = features.timeSignature ?: return null
            return "$id $danceability $energy $key $loudness $mode $speechiness $acousticness $instrumentalness $liveness $valence $tempo $durationMs $timeSignature\n"
        }
    }
}

fun main(args: Array<String>) {
    FeatureRipper.runRipper()
}