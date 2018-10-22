package org.stevenlowes.project.spotifyAPI

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.exceptions.detailed.ForbiddenException
import com.wrapper.spotify.model_objects.miscellaneous.AudioAnalysis
import com.wrapper.spotify.model_objects.specification.AudioFeatures
import com.wrapper.spotify.model_objects.specification.Track

class Spotify {
    companion object {
        val api: SpotifyApi = SpotifyApi.builder()
                .setClientId(SpotifyAuth.clientId)
                .setClientSecret(SpotifyAuth.clientSecret)
                .setRedirectUri(SpotifyAuth.redirectUri).build()

        fun seekToStart() {
            try {
                api.pauseUsersPlayback().build().execute()
            }
            catch(ignore: ForbiddenException){}

            api.seekToPositionInCurrentlyPlayingTrack(0).build().execute()
        }

        fun play(){
            api.startResumeUsersPlayback().build().execute()
        }

        fun currentlyPlaying(): Track {
            val currentlyPlaying = api.usersCurrentlyPlayingTrack.build().execute()!!
            return currentlyPlaying.item
        }

        fun getTrack(id: String): Track {
            return api.getTrack(id).build().execute()!!
        }

        fun getAnalysedTrack(id: String): AnalysedTrack {
            val trackFuture= api.getTrack(id).build().executeAsync<Track>()
            val featuresFuture = api.getAudioFeaturesForTrack(id).build().executeAsync<AudioFeatures>()
            val analysisFuture = api.getAudioAnalysisForTrack(id).build().executeAsync<AudioAnalysis>()

            val track = trackFuture.get()
            val features = featuresFuture.get()
            val analysis = analysisFuture.get()

            return AnalysedTrack(track, features, analysis)
        }

        fun getAnalysedTrackFromLink(songLink: String): AnalysedTrack{
            val id = songLink.substringAfterLast("/track/").take(22)
            return getAnalysedTrack(id)
        }
    }
}