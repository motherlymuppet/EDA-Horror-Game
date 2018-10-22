package org.stevenlowes.project.nearestneighbour

import org.stevenlowes.project.spotifyAPI.AnalysedTrack

data class TrackVector(val id: String?,
                       val popularity: Int,
                       val key: Int,
                       val mode: Int,
                       val timeSignature: Int,
                       val tempo: Float,
                       val acousticness: Float,
                       val danceability: Float,
                       val energy: Float,
                       val instrumentalness: Float,
                       val speechiness: Float,
                       val liveness: Float,
                       val loudness: Float,
                       val valence: Float) {
    companion object {
        fun create(track: AnalysedTrack) = TrackVector(
                track.id,
                track.track.popularity,
                track.features.key,
                track.features.mode.mode,
                track.features.timeSignature,
                track.features.tempo,
                track.features.acousticness,
                track.features.danceability,
                track.features.energy,
                track.features.instrumentalness,
                track.features.speechiness,
                track.features.liveness,
                track.features.loudness,
                track.features.valence
                                                                                                                              )
    }
}