package org.stevenlowes.project.spotifyAPI

import de.vandermeer.asciitable.AsciiTable
import de.vandermeer.asciitable.CWC_LongestLine
import org.stevenlowes.project.spotifyAPI.Util.Companion.readString
import org.stevenlowes.project.spotifyAPI.scrapers.ManualUse
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

        val importance = listOf(
                5f, //Danceability
                5f, //Energy
                0f, //Keyc
                0f, //Loudness
                0f, //Mode
                1.5f, //Speechiness
                0.5f, //Acousticness
                0.5f, //Instrumentalness
                0f, //Liveness
                2f, //Valence
                3f, //Tempo
                0f, //DurationMs
                0f, //TimeSignature
                3f, //AverageSegmentDurationSecs
                0f, //Timbre1
                1f, //Timbre2
                .75f, //Timbre3
                1f, //Timbre4
                .5f, //Timbre5
                .5f, //Timbre6
                1f, //Timbre7
                .25f, //Timbre8
                .5f, //Timbre9
                .5f, //Timbre10
                .5f, //Timbre11
                0f //Timbre12
                                       )

        fun applyImportance(vector: FloatArray): FloatArray = vector.mapIndexed {index, value -> applyImportance(index, value)}.toFloatArray()

        fun applyImportance(vector: List<Float>): FloatArray = vector.mapIndexed {index, value -> applyImportance(index, value)}.toFloatArray()

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
            seq.mapNotNull {
                val (id, vector) = readString(it)
                val important = applyImportance(vector)
                id to important
            }
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
    fun nearest(to: FloatArray, bannedIds: List<String> = listOf()): Pair<String, Float> {
        val toWithImportance = applyImportance(to)
        return ids.asSequence()
                .zip(data.asSequence())
                .filter { (id, _) -> id !in bannedIds }
                .map { (id, array) -> id to distance(array, toWithImportance) }
                .filter { (_, distance) -> distance != 0f } //Prevent returning an identical song (this can be removed once we are actually using this TODO
                .minBy { (_, distance) -> distance }!!
    }
}

fun distance(a1: FloatArray, a2: FloatArray): Float {
    //return maxDistance(a1, a2) + 0.5f * actualDistance(a1, a2)
    //return maxDistance(a1, a2)
    return actualDistance(a1, a2)
    //return cityBlockDistance(a1, a2)
}

fun cityBlockDistance(a1: FloatArray, a2: FloatArray) = a1.zip(a2).asSequence()
        .map { (a, b) -> abs(a-b) }
        .sum()

fun actualDistance(a1: FloatArray, a2: FloatArray) = sqrt(a1.zip(a2)
                        .asSequence()
                        .map { (a, b) -> a - b }
                        .map { dif -> dif * dif }
                        .sum())

fun maxDistance(a1: FloatArray, a2: FloatArray) = a1.zip(a2).asSequence()
        .map { (a, b) -> abs(a-b) }
        .max()!!


fun main(args: Array<String>) {
    SpotifyAuth.refreshAuth()

    val data = DataContainer()

    val scanner = Scanner(System.`in`)
    while(true){
        SpotifyAuth.refreshAuth()

        val playing = Spotify.getAnalysedTrack(Spotify.currentlyPlaying().id)
        val playingArray = ManualUse.translate(playing)

        val withImportance = DataContainer.applyImportance(playingArray)

        val bannedIds = listOf<String>()

        val (nearestId, distance) = data.nearest(playingArray, bannedIds)
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
        println("Original Preview: ${playing.track.previewUrl}")
        println("Nearest Preview: ${Spotify.api.getTrack(nearestId).build().execute().previewUrl}")
        println()
        println("Original: https://open.spotify.com/trackProperty/${playing.id}")
        println("Nearest: https://open.spotify.com/trackProperty/$nearestId")
        println()
        println("Distance: $distance")
        println()

        val table = AsciiTable()
        table.renderer.cwc = CWC_LongestLine()
        table.addRow("Value", "Searched", "Found", "Difference")
        names.zip(withImportance.zip(nearestArray)).forEachIndexed { index, (name, pair) ->
            val importance = DataContainer.importance[index]
            if(importance != 0f){
                val (searched, found) = pair
                table.addRow(name, searched/importance, found/importance, abs(pair.first - pair.second))
            }
        }
        println(table.render())

        scanner.nextLine()
    }
}
