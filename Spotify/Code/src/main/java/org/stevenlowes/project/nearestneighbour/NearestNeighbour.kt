package org.stevenlowes.project.nearestneighbour

import com.neovisionaries.i18n.CountryCode
import com.wrapper.spotify.exceptions.SpotifyWebApiException
import com.wrapper.spotify.exceptions.detailed.TooManyRequestsException
import com.wrapper.spotify.model_objects.specification.TrackSimplified
import org.stevenlowes.project.previewplayer.PreviewPlayer
import org.stevenlowes.project.spotifyAPI.Spotify
import org.stevenlowes.project.spotifyAPI.SpotifyAuth

class NearestNeighbour {
    companion object {
        fun getNearest(vector: TrackVector, invalidTrackIds: List<String>): TrackSimplified {
            var limit = 1

            var searchUpper = 0.3f
            var searchLower = 0f

            var allowance = 0.15f

            val seedGenres: List<String> = listOf(
                    "acoustic",
                    "afrobeat",
                    "alt-rock",
                    "alternative",
                    "ambient",
                    "anime",
                    "black-metal",
                    "bluegrass",
                    "blues",
                    "bossanova",
                    "brazil",
                    "breakbeat",
                    "british",
                    "cantopop",
                    "chicago-house",
                    "children",
                    "chill",
                    "classical",
                    "club",
                    "comedy",
                    "country",
                    "dance",
                    "dancehall",
                    "death-metal",
                    "deep-house",
                    "detroit-techno",
                    "disco",
                    "disney",
                    "drum-and-bass",
                    "dub",
                    "dubstep",
                    "edm",
                    "electro",
                    "electronic",
                    "emo",
                    "folk",
                    "forro",
                    "french",
                    "funk",
                    "garage",
                    "german",
                    "gospel",
                    "goth",
                    "grindcore",
                    "groove",
                    "grunge",
                    "guitar",
                    "happy",
                    "hard-rock",
                    "hardcore",
                    "hardstyle",
                    "heavy-metal",
                    "hip-hop",
                    "holidays",
                    "honky-tonk",
                    "house",
                    "idm",
                    "indian",
                    "indie",
                    "indie-pop",
                    "industrial",
                    "iranian",
                    "j-dance",
                    "j-idol",
                    "j-pop",
                    "j-rock",
                    "jazz",
                    "k-pop",
                    "kids",
                    "latin",
                    "latino",
                    "malay",
                    "mandopop",
                    "metal",
                    "metal-misc",
                    "metalcore",
                    "minimal-techno",
                    "movies",
                    "mpb",
                    "new-age",
                    "new-release",
                    "opera",
                    "pagode",
                    "party",
                    "philippines-opm",
                    "piano",
                    "pop",
                    "pop-film",
                    "post-dubstep",
                    "power-pop",
                    "progressive-house",
                    "psych-rock",
                    "punk",
                    "punk-rock",
                    "r-n-b",
                    "rainy-day",
                    "reggae",
                    "reggaeton",
                    "road-trip",
                    "rock",
                    "rock-n-roll",
                    "rockabilly",
                    "romance",
                    "sad",
                    "salsa",
                    "samba",
                    "sertanejo",
                    "configure-tunes",
                    "singer-songwriter",
                    "ska",
                    "sleep",
                    "songwriter",
                    "soul",
                    "soundtracks",
                    "spanish",
                    "study",
                    "summer",
                    "swedish",
                    "synth-pop",
                    "tango",
                    "techno",
                    "trance",
                    "trip-hop",
                    "turkish",
                    "work-out",
                    "world-music").asSequence().chunked(5).map { it.joinToString(",") }.toList()

            println("Starting")

            while (true) {
                try {
                    if(allowance > 0.25f && searchUpper == 0.3f){
                        searchUpper = 1f
                    }

                    val recommendations = mutableListOf<TrackSimplified>()
                    seedGenres.forEach { seedGenre ->
                        try {
                            val new = getRecommendations(
                                    vector = vector,
                                    seedGenre = seedGenre,
                                    limit = limit,
                                    allowance = allowance)
                            if (new != null) {
                                recommendations.addAll(new)
                            }
                        }
                        catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }

                    val filtered = recommendations.filter {
                        !it.isExplicit &&
                                !it.previewUrl.isNullOrBlank() &&
                                it.id !in invalidTrackIds
                    }

                    println("${filtered.size} valid recommendations")
                    filtered.forEach {
                        println(it.previewUrl)
                    }

                    if(filtered.isNotEmpty() && searchUpper - searchLower < 0.001f){
                        println("Assuming all valid songs are the same due to tiny search delta")
                        return filtered.first()
                    }

                    val validCount = filtered.size
                    when (validCount) {
                        1 -> return filtered.first()
                        0 -> if (recommendations.size == limit) {
                            //There are results but no valid results, but there may be more available that we didn't get
                            //Don't change the bounds, just change the number of results that we request
                            limit *= 4
                            println(limit)
                        }
                        else {
                            //There were no valid results and we had retrieved all available
                            searchLower = allowance
                            allowance = (allowance + searchUpper) / 2
                            limit = 1
                            println("New bounds: $searchLower $allowance $searchUpper")
                            println()
                        }
                        else -> {
                            searchUpper = allowance
                            allowance = (allowance + searchLower) / 2
                            limit = 1
                            println("New bounds: $searchLower $allowance $searchUpper")
                            println()
                        }
                    }
                }
                catch (ex: TooManyRequestsException) {
                    println("Sleeping")
                    Thread.sleep(3000)
                }
                catch (ex: SpotifyWebApiException) {
                    println("Exception")
                    ex.printStackTrace()
                }
                catch (ex: Exception) {
                    println("Other exception")
                    ex.printStackTrace()
                }
            }
        }

        private fun getRecommendations(vector: TrackVector,
                                       seedGenre: String,
                                       limit: Int, allowance: Float): Array<out TrackSimplified>? {
            val recommendations = Spotify.api.recommendations.limit(limit)
                    .seed_genres(seedGenre)
                    .market(CountryCode.GB)

                    //.max_duration_ms(maxDurationMs)
                    //.target_popularity(vector.popularity)

                    .min_mode(vector.mode)
                    .max_mode(vector.mode)
                    .min_key(vector.key)
                    .max_key(vector.key)
                    .min_time_signature(vector.timeSignature)
                    .max_time_signature(vector.timeSignature)

                    .min_danceability(vector.danceability - allowance)
                    .max_danceability(vector.danceability + allowance)
                    .min_energy(vector.energy - allowance)
                    .max_energy(vector.energy + allowance)
                    .min_valence(vector.valence - allowance)
                    .max_valence(vector.valence + allowance)


                    //.min_acousticness(vector.acousticness * allowance)
                    //.max_acousticness(vector.acousticness * upperBound)
                    // .min_instrumentalness(vector.instrumentalness * allowance)
                    // .max_instrumentalness(vector.instrumentalness * upperBound)
                    //.target_liveness(vector.liveness)
                    //.min_liveness(vector.liveness * allowance)
                    //.max_liveness(vector.liveness * upperBound)
                    //.min_loudness(vector.loudness * upperBound)
                    //.max_loudness(vector.loudness * allowance)
                    //.target_speechiness(vector.speechiness)
                    //.min_speechiness(vector.speechiness * allowance)
                    //.max_speechiness(vector.speechiness * upperBound)
                    //.min_tempo(vector.tempo - allowance * 100)
                    //.max_tempo(vector.tempo + allowance * 100)


                    .build()
                    .execute()

            return recommendations?.tracks
        }
    }
}