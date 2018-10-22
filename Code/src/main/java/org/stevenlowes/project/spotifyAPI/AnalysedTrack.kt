package org.stevenlowes.project.spotifyAPI

import com.wrapper.spotify.model_objects.miscellaneous.AudioAnalysis
import com.wrapper.spotify.model_objects.specification.AudioFeatures
import com.wrapper.spotify.model_objects.specification.Track

data class AnalysedTrack(val track: Track,
                         val features: AudioFeatures,
                         val analysis: AudioAnalysis){
    val id = track.id
    val name = track.name
    val artist = track.artists.joinToString(",")
    val album = track.album.name
}