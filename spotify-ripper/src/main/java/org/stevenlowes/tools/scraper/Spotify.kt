package org.stevenlowes.tools.scraper

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.exceptions.detailed.ForbiddenException
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

        fun getFeatures(track: Track): AudioFeatures = api.getAudioFeaturesForTrack(track.id).build().executeAsync<AudioFeatures>().get()
    }
}