package org.stevenlowes.project.spotifyAPI

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.model_objects.miscellaneous.AudioAnalysis
import com.wrapper.spotify.model_objects.specification.*
import tornadofx.*
import java.util.*

class Spotify {
    companion object {
        private val bodgePlaylistId = "4IvDNhvA1GDggmipuHqrvZ"

        val api: SpotifyApi = SpotifyApi.builder()
                .setClientId(SpotifyAuth.clientId)
                .setClientSecret(SpotifyAuth.clientSecret)
                .setRedirectUri(SpotifyAuth.redirectUri).build()

        fun play(id: String, maxLengthMs: Long? = null, onFinish: () -> Unit = {}) {
            val playlist = api.getPlaylist(bodgePlaylistId).build().execute()

            api.addTracksToPlaylist(bodgePlaylistId, arrayOf("spotify:track:$id")).build().execute()
            api.skipUsersPlaybackToNextTrack().build().execute()
            api.startResumeUsersPlayback().build().execute()

            val startPlayTime = System.currentTimeMillis()
            val endPlayTime = startPlayTime + getTrack(id).durationMs

            val array = JsonArray()
            playlist.tracks.items
                    .forEach {
                        val track = JsonObject()
                        track.addProperty("uri", "spotify:track:${it.track.id}")
                        array.add(track)
                    }

            api.removeTracksFromPlaylist(bodgePlaylistId, array).build().execute()

            val timer = Timer()
            val task = object : TimerTask() {
                override fun run() {
                    api.pauseUsersPlayback().build().execute()
                    onFinish()
                }
            }

            val now = System.currentTimeMillis()
            val sleepFor = endPlayTime - now

            val actualDelay = Math.min(sleepFor, maxLengthMs ?: Long.MAX_VALUE)

            timer.schedule(task, actualDelay)
        }

        fun seekToPercent(percent: Double){
            val track = currentlyPlaying()
            api.seekToPositionInCurrentlyPlayingTrack((track.durationMs*percent).toInt()).build().execute()
        }

        fun currentlyPlaying(): Track {
            val currentlyPlaying = api.usersCurrentlyPlayingTrack.build().execute()!!
            return currentlyPlaying.item
        }

        fun getTrack(id: String): Track {
            return api.getTrack(id).build().execute()!!
        }

        fun getAnalysedTrack(id: String): AnalysedTrack {
            val trackFuture = api.getTrack(id).build().executeAsync<Track>()
            val featuresFuture = api.getAudioFeaturesForTrack(id).build().executeAsync<AudioFeatures>()
            val analysisFuture = api.getAudioAnalysisForTrack(id).build().executeAsync<AudioAnalysis>()

            val track = trackFuture.get()
            val features = featuresFuture.get()
            val analysis = analysisFuture.get()

            return AnalysedTrack(track, features, analysis)
        }

        fun getAnalysedTrackFromLink(songLink: String): AnalysedTrack {
            val id = songLink.substringAfterLast("/track/").take(22)
            return getAnalysedTrack(id)
        }

        fun getPlaylists(): List<PlaylistSimplified> {
            val user = api.currentUsersProfile.build().execute()
            return api.getListOfUsersPlaylists(user.id).build().execute().items.toList()
        }

        fun getPlaylist(playlistId: String): List<PlaylistTrack>{
            return api.getPlaylist(playlistId).build().execute().tracks.items.toList()
        }

        fun pause(){
            api.pauseUsersPlayback().build().executeAsync<String>()
        }
    }
}
