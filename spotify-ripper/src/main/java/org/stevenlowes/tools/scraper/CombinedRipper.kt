package org.stevenlowes.tools.scraper

import com.wrapper.spotify.model_objects.miscellaneous.AudioAnalysis
import com.wrapper.spotify.model_objects.specification.AudioFeatures
import org.stevenlowes.tools.scraper.rippers.AnalysisRipper
import org.stevenlowes.tools.scraper.rippers.FeatureRipper

class CombinedRipper{
    companion object{
        fun getString(id: String): String {
            val featuresFuture = Spotify.api.getAudioFeaturesForTrack(id).build().executeAsync<AudioFeatures>()
            val analysisFuture = Spotify.api.getAudioAnalysisForTrack(id).build().executeAsync<AudioAnalysis>()

            val features = featuresFuture.get()
            val analysis = analysisFuture.get()

            return AnalysisRipper.getLine(FeatureRipper.getLine(features)!!, analysis)!!
        }
    }
}