package org.stevenlowes.tools.scraper.rippers

import org.jetbrains.annotations.Contract
import java.io.*
import java.math.BigDecimal
import java.math.MathContext

class PreProcessor {
    companion object {
        @Contract(pure = true)
        fun readString(string: String): Pair<String, List<Float>>? {
            val split = string.split(" ")

            val id = split[0]
            val values = split
                    .asSequence()
                    .drop(1)
                    .map { it.toFloat() }
                    .toList()

            return id to values
        }

        @Contract(pure = false)
        fun apply() {
            println("Gathering sum info")
            val firstReader = BufferedReader(FileReader("filteredData.txt"))
            val (count, sum, sqSum) = readStdDev(firstReader)
            println("Done gathering sum info")

            val bdMeans = sum.map { it.divide(count.toBigDecimal(), MathContext.DECIMAL128) }
            val stdDevs = (sqSum.map {
                it.divide(count.toBigDecimal(), MathContext.DECIMAL128)
            } - (bdMeans.map { it * it })).map { it.sqrt(MathContext.DECIMAL128).toDouble() }
            val means = bdMeans.map { it.toDouble() }

            println("Translating file")
            val secondReader = BufferedReader(FileReader("filteredData.txt"))
            val writer = FileWriter("processedData.txt")
            writeProcessedData(secondReader, writer, means, stdDevs)
            println("Done Translating file")
        }

        @Contract(pure = false)
        private fun readStdDev(input: Reader) = input.useLines { lines ->
            calculateStdDev(lines)
        }

        @Contract(pure = false)
        private fun calculateStdDev(lines: Sequence<String>): Triple<Int, List<BigDecimal>, List<BigDecimal>> {
            return lines.mapNotNull { readString(it)?.second }
                    .fold(Triple(0, List(26) { BigDecimal.ZERO }, List(26) { BigDecimal.ZERO }))
                    { (count, sum, sqSum), next ->
                        val newSum = sum + next
                        val newSqSum = sqSum + next.map { it * it }
                        Triple(count + 1, newSum, newSqSum)
                    }
        }

        @Contract(pure = true)
        private operator fun List<BigDecimal>.plus(other: List<Float>) = zip(other).map { (a, b) -> a.add(b.toBigDecimal(), MathContext.DECIMAL128) }

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
                lines.mapNotNull(PreProcessor.Companion::readString)
                        .map { (id, values) ->
                            id to translateToZScore(values, means, stdDevs)
                        }

        @Contract(pure = true)
        private fun translateToZScore(floats: List<Float>, means: List<Double>, stdDevs: List<Double>) = floats
                    .zip(means)
                    .zip(stdDevs) { (a, b), c -> Triple(a.toDouble(), b, c) }
                    .map { (value, mean, stdDev) ->
                        ((value - mean) / stdDev).toFloat()
                    }
    }
}

fun main(args: Array<String>) {
    PreProcessor.apply()
}