package org.stevenlowes.project.spotifyAPI

import org.stevenlowes.project.spotifyAPI.Util.Companion.readString
import java.io.BufferedReader
import java.io.FileReader
import java.util.*
import java.util.stream.Stream
import kotlin.math.abs
import kotlin.math.sqrt

class DataContainer {
    val data: Array<FloatArray>
    val ids: Array<String>
    private val rand = Random()

    operator fun get(index: Int): Pair<String, FloatArray> {
        return ids[index] to data[index]
    }

    fun randomId() = ids[rand.nextInt(ids.size)]

    companion object {
        val instance: DataContainer by lazy { DataContainer() }

        val bannedSongs = mutableListOf<String>()

        val importance = listOf(
                5f, //Danceability
                5f, //Energy
                0f, //Key
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

        fun applyImportance(vector: FloatArray): FloatArray = vector.mapIndexed { index, value ->
            applyImportance(index,
                            value)
        }.toFloatArray()

        fun applyImportance(vector: List<Float>): FloatArray = vector.mapIndexed { index, value ->
            applyImportance(index,
                            value)
        }.toFloatArray()

        private fun applyImportance(index: Int, value: Float) = value * importance[index]

        private fun distance(a1: FloatArray, a2: FloatArray) =
                sqrt(
                        a1.zip(a2)
                                .fold(0f) { acc, (a, b) ->
                                    val dif = (a - b)
                                    acc + dif * dif
                                }
                    )
    }

    fun distance(a1: String, a2: String) = distance(getVector(a1), getVector(a2))

    private fun getVector(id: String) = data[ids.indexOf(id)]

    init {
        println("Loading dataset into memory")

        val count = BufferedReader(FileReader("processedData.txt")).useLines { seq ->
            seq.count()
        }

        println("$count songs")

        data = Array(count) { _ -> FloatArray(26) }
        ids = Array(count) { "" }

        var done = 0
        val percentCount = count / 100
        BufferedReader(FileReader("processedData.txt")).useLines { seq ->
            seq.mapNotNull {
                val (id, vector) = readString(it)
                val important = applyImportance(vector)
                done++
                if (done % percentCount == 0) {
                    println("${done / percentCount}%")
                }
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
     * Returns the id of the song nearest targetId the point specified by the vector `targetId`
     */
    fun nearest(targetId: String, bannedIds: List<String> = listOf()): String {
        return distances(targetId, bannedIds)
                .filter { (_, distance) -> distance > 0.1f }
                .min { o1, o2 -> o1.second.compareTo(o2.second) }.get().first
    }

    /**
     * Returns the id of the song further from the point specified by the vector `target`
     */
    fun furthest(targetId: String, bannedIds: List<String> = listOf()): String {
        return distances(targetId, bannedIds).max { o1, o2 -> o1.second.compareTo(o2.second) }.get().first
    }

    fun closestToDistance(targetDistance: Float, targetId: String, bannedIds: List<String> = listOf()): String {
        return distances(targetId,
                         bannedIds).min { o1, o2 -> (abs(o1.second - targetDistance)).compareTo(abs(o2.second - targetDistance)) }.get().first
    }

    private fun distances(targetId: String, bannedIds: List<String>): Stream<Pair<String, Float>> {
        val targetVector = getVector(targetId)
        return ids.zip(data).parallelStream()
                .filter { (id, _) -> id !in bannedIds && id !in bannedSongs && id != targetId }
                .map { (id, vector) -> id to distance(targetVector, vector) }
    }
}
