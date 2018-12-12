package org.stevenlowes.project.spotifyAPI.scrapers

import org.jetbrains.annotations.Contract
import org.stevenlowes.project.spotifyAPI.AnalysedTrack
import org.stevenlowes.project.spotifyAPI.Util
import java.io.*

class PreProcessor{
    companion object {
        @Contract(pure = false)
        fun translateFile(){
            val reader = BufferedReader(FileReader("stdDevConfig.txt"))
            val (means, stdDevs) = readStdDevs(reader)

            println("Translating file")
            val secondReader = BufferedReader(FileReader("filteredData.txt"))
            val writer = FileWriter("processedData.txt")
            writeProcessedData(secondReader, writer, means, stdDevs)
            println("Done Translating file")
        }

        @Contract(pure = false)
        internal fun readStdDevs(input: BufferedReader) = input.use { reader ->
            val meansLine = reader.readLine().split(" ").map { it.toDouble() }
            val stdDevsLine = reader.readLine().split(" ").map { it.toDouble() }
            return@use meansLine to stdDevsLine
        }

        @Contract(pure = false)
        private fun writeProcessedData(input: Reader, output: Writer, means: List<Double>, stdDevs: List<Double>) {
            input.useLines { seq ->
                output.use { writer ->
                    translateToZScores(seq, means, stdDevs).forEach { (id, stdDevs) ->
                        writer.write("$id ${stdDevs.joinToString(" ")}\n")
                    }
                }
            }
        }

        @Contract(pure = false)
        private fun translateToZScores(lines: Sequence<String>, means: List<Double>, stdDevs: List<Double>) =
                lines.mapNotNull(Util.Companion::readString)
                        .map { (id, values) ->
                            id to translateToZScore(values, means, stdDevs)
                        }

        @Contract(pure = true)
        internal fun translateToZScore(floats: List<Float>, means: List<Double>, stdDevs: List<Double>) = floats
                .zip(means)
                .zip(stdDevs) { (a, b), c -> Triple(a.toDouble(), b, c) }
                .map { (value, mean, stdDev) ->
                    ((value - mean) / stdDev).toFloat()
                }
    }
}

fun main(args: Array<String>){
    PreProcessor.translateFile()
}