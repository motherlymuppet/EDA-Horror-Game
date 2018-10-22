package org.stevenlowes.tools.scraper

import de.vandermeer.asciitable.AsciiTable
import de.vandermeer.asciitable.CWC_LongestLine
import de.vandermeer.skb.interfaces.document.TableRowStyle
import java.io.BufferedReader
import java.io.FileReader
import java.util.*
import kotlin.math.abs
import kotlin.math.sqrt

class DataContainer {
    val data: Array<FloatArray>
    val ids: Array<String>

    operator fun get(index: Int): Pair<String, FloatArray> {
        return ids[index] to data[index]
    }

    companion object {
        fun readString(string: String): Pair<String, FloatArray>? {
            val split = string.split(" ")
            if (split[1] == "error") {
                return null
            }

            val id = split[0]
            val values: FloatArray = split
                    .asSequence()
                    .drop(1)
                    .mapIndexed { index, value -> applyImportance(index, value.toFloat()) }
                    .toList().toFloatArray()

            return id to values
        }

        private val importance = listOf(
                1f, //Danceability
                2f, //Energy
                0f, //Key
                0f, //Loudness
                0f, //Mode
                0f, //Speechiness
                0f, //Acousticness
                0f, //Instrumentalness
                0f, //Liveness
                1f, //Valence
                2f, //Tempo
                0f, //DurationMs
                0f, //TimeSignature
                .2f, //AverageSegmentDurationSecs
                0f, //Timbre1
                .3f, //Timbre2
                .6f, //Timbre3
                .6f, //Timbre4
                .3f, //Timbre5
                .6f, //Timbre6
                .3f, //Timbre7
                1f, //Timbre8
                .3f, //Timbre9
                1f, //Timbre10
                1f, //Timbre11
                .3f //Timbre12
                                       )

        private fun applyImportance(index: Int, value: Float) = value * importance[index]
    }

    init {
        println("Loading dataset into memory")

        val count = BufferedReader(FileReader("processedData.txt")).useLines { seq ->
            seq.count()
        }

        data = Array(count) { _ -> FloatArray(26) }
        ids = Array(count) { "" }

        BufferedReader(FileReader("processedData.txt")).useLines { seq ->
            seq.mapNotNull { string -> readString(string) }
                    .filter { it.second[8] < 0.1f }
                    .forEachIndexed { index, (id, result) ->
                        ids[index] = id
                        data[index] = result
                    }
        }

        println("Finished loading dataset")
    }

    /**
     * Returns the id of the song nearest to the point specified by the vector `to`
     */
    fun nearest(to: FloatArray, bannedIds: List<String> = listOf()) =
            ids.asSequence()
                    .zip(data.asSequence())
                    .filter { (id, _) -> id !in bannedIds }
                    .map { (id, array) -> id to distance(array, to) }
                    .filter { (_, distance) -> distance != 0f } //Prevent returning an identical song (this can be removed once we are actually using this TODO
                    .minBy { (_, distance) -> distance }!!
                    .first
}

fun distance(a1: FloatArray, a2: FloatArray): Float {
    return sqrt(a1.zip(a2)
                        .asSequence()
                        .map { (a, b) -> a - b }
                        .map { dif -> dif * dif }
                        .sum())
}

fun main(args: Array<String>) {
    SpotifyAuth.manualAuth("AQAOuwxSwwmBLmFFYOx4VLURaes8e2lRdscJWLKnkvT8A5lkM95YVzIKFWayqIylXmr7m3iqdx_ND1aruS41U7wEH33S8wLSUElHllT16Ot8s_P_63WaivO9TgEybcizN3F9fg")

    val data = DataContainer()

    val random = Random()
    val randomIndex = random.nextInt(data.ids.count { it.isNotEmpty() })

    val (playingId, playingArray) = data[
            randomIndex
            //data.ids.indexOf("1pKYYY0dkg23sQQXi0Q5zN")
    ]

    println(playingId)
    val playing = Spotify.getTrack(playingId)

    val bannedIds = listOf<String>()

    val nearestId = data.nearest(playingArray, bannedIds)
    val nearestArray = data.data[data.ids.indexOf(nearestId)]

    val names = listOf(
            "Danceability",
            "Energy",
            "Key",
            "Loudness",
            "Mode",
            "Speechiness",
            "Acousticness",
            "Instrumentalness",
            "Liveness",
            "Valence",
            "Tempo",
            "DurationMs",
            "TimeSignature",
            "AverageSegmentDurationSecs",
            "Timbre1",
            "Timbre2",
            "Timbre3",
            "Timbre4",
            "Timbre5",
            "Timbre6",
            "Timbre7",
            "Timbre8",
            "Timbre9",
            "Timbre10",
            "Timbre11",
            "Timbre12")

    println()
    println("Original Preview: ${playing.previewUrl}")
    println("Nearest Preview: ${Spotify.api.getTrack(nearestId).build().execute().previewUrl}")
    println()
    println("Original: https://open.spotify.com/track/${playing.id}")
    println("Nearest: https://open.spotify.com/track/$nearestId")
    println()
    println("Distance: ${distance(playingArray, nearestArray)}")
    println()

    val table = AsciiTable()
    table.renderer.cwc = CWC_LongestLine()
    table.addRow("Value", "Searched", "Found", "Difference")
    names.zip(playingArray.zip(nearestArray)).forEach { (name, pair) ->
        table.addRow(name, pair.first, pair.second, abs(pair.first - pair.second))
    }
    println(table.render())
}
