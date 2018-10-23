package org.stevenlowes.project.spotifyAPI

import org.jetbrains.annotations.Contract

class Util{
    companion object {
        @Contract(pure = true)
        fun readString(string: String): Pair<String, List<Float>> {
            val split = string.split(" ")

            val id = split[0]
            val values = split
                    .asSequence()
                    .drop(1)
                    .map { it.toFloat() }
                    .toList()

            return id to values
        }
    }
}