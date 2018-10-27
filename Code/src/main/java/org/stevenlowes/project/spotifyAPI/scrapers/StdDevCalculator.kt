package org.stevenlowes.project.spotifyAPI.scrapers

import org.jetbrains.annotations.Contract
import org.stevenlowes.project.spotifyAPI.Util.Companion.readString
import java.io.BufferedReader
import java.io.FileReader
import java.io.FileWriter
import java.io.Reader
import java.math.BigDecimal
import java.math.MathContext

class StdDevCalculator {
    companion object {
        @Contract(pure = false)
        fun calculate() {
            println("Gathering sum info")
            val firstReader = BufferedReader(FileReader("filteredData.txt"))
            val (count, sum, sqSum) = readStdDev(firstReader)
            println("Done gathering sum info")

            val bdMeans = sum.map { it.divide(count.toBigDecimal(), MathContext.DECIMAL128) }
            val stdDevs = (sqSum.map {
                it.divide(count.toBigDecimal(), MathContext.DECIMAL128)
            } - (bdMeans.map { it * it })).map { it.sqrt(MathContext.DECIMAL128).toDouble() }
            val means = bdMeans.map { it.toDouble() }

            val configWriter = FileWriter("stdDevConfig.txt")
            writeConfigData(configWriter, means, stdDevs)
        }

        private fun writeConfigData(writer: FileWriter, means: List<Double>, stdDevs: List<Double>) = writer.use {
            writer.write("${means.joinToString(" ")}\n")
            writer.write("${stdDevs.joinToString(" ")}\n")
        }

        @Contract(pure = false)
        private fun readStdDev(input: Reader) = input.useLines { lines ->
            calculateStdDev(lines)
        }

        @Contract(pure = false)
        private fun calculateStdDev(lines: Sequence<String>) = lines.mapNotNull { readString(it).second }
                .fold(Triple(0, List(26) { BigDecimal.ZERO }, List(26) { BigDecimal.ZERO }))
                { (count, sum, sqSum), next ->
                    val newSum = sum + next
                    val newSqSum = sqSum + next.map { it * it }
                    Triple(count + 1, newSum, newSqSum)
                }

        @Contract(pure = true)
        private operator fun List<BigDecimal>.plus(other: List<Float>) = zip(other).map { (a, b) ->
            a.add(b.toBigDecimal(),
                  MathContext.DECIMAL128)
        }
    }
}

fun BigDecimal.sqrt(context: MathContext): BigDecimal {
    val two = BigDecimal("2")
    var x0 = BigDecimal.ZERO
    var x1 = BigDecimal(Math.sqrt(toDouble()))
    while (x0 != x1) {
        x0 = x1
        x1 = divide(x0, context)
        x1 = x1.add(x0)
        x1 = x1.divide(two, context)
    }
    return x1
}

fun main(args: Array<String>) {
    StdDevCalculator.calculate()
}