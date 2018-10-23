package org.stevenlowes.project.spotifyAPI.scrapers

import org.jetbrains.annotations.Contract
import org.stevenlowes.project.spotifyAPI.AnalysedTrack
import org.stevenlowes.project.spotifyAPI.Util.Companion.readString
import java.io.BufferedReader
import java.io.FileReader

class ManualUse {
    companion object {
        fun translate(track: AnalysedTrack): FloatArray {
            return translateTracks(sequenceOf(track)).first()
        }

        fun translate(tracks: List<AnalysedTrack>): List<FloatArray>{
            return translateTracks(tracks.asSequence()).toList()
        }

        @Contract(pure = true)
        private fun translateTracks(trackSequence: Sequence<AnalysedTrack>): Sequence<FloatArray> {
            val vectorSequence = trackSequence.asSequence()
                    .map { AnalysisRipper.getAnalysisString(it) }
                    .map { readString(it) }
                    .map { it.second }
            return translateVectors(vectorSequence)
        }

        @Contract(pure = true)
        private fun translateVectors(vectors: Sequence<List<Float>>): Sequence<FloatArray> {
            val reader = BufferedReader(FileReader("stdDevConfig.txt"))
            val (means, stdDevs) = PreProcessor.readStdDevs(reader)
            return vectors.map { value -> PreProcessor.translateToZScore(value, means, stdDevs) }
                    .map { it.toFloatArray() }
        }
    }
}