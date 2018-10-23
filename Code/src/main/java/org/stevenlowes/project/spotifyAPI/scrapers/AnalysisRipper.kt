package org.stevenlowes.project.spotifyAPI.scrapers

import com.wrapper.spotify.exceptions.detailed.BadGatewayException
import com.wrapper.spotify.exceptions.detailed.NotFoundException
import com.wrapper.spotify.exceptions.detailed.TooManyRequestsException
import com.wrapper.spotify.model_objects.miscellaneous.AudioAnalysis
import com.wrapper.spotify.model_objects.specification.AudioFeatures
import org.stevenlowes.project.spotifyAPI.AnalysedTrack
import org.stevenlowes.project.spotifyAPI.Spotify
import org.stevenlowes.project.spotifyAPI.SpotifyAuth
import java.io.*
import java.util.concurrent.ExecutionException

fun String.id(): String = substringBefore(" ")

class AnalysisRipper {
    companion object {
        fun runRipper(simultaneousPages: Int = 10) {
            val doneCount = Incrementer()

            val alreadyDone = mutableSetOf<String>()

            println("Initialising set")

            try {
                BufferedReader(FileReader("analysedData.txt"))
                        .useLines { seq -> seq.forEach { alreadyDone.add(it.id()) } }
            }
            catch (e: FileNotFoundException) {
                println("No data to exclude")
            }

            println("Done initialising set")

            BufferedReader(FileReader("featuredData.txt")).use { featureFile ->
                FileWriter("analysedData.txt", true).use { analysisFile ->
                    val fileIdIterator = object : Iterator<String> {
                        var nextLine = featureFile.readLine()

                        override fun hasNext(): Boolean {
                            return nextLine != null
                        }

                        override fun next(): String {
                            val returnValue = nextLine
                            nextLine = featureFile.readLine()
                            return returnValue
                        }
                    }

                    val featureStringSequence = RetrySequence<String>(fileIdIterator)

                    val procs = Runtime.getRuntime().availableProcessors()
                    val threads = (1..procs).map {
                        Thread(Runner(doneCount,
                                      featureStringSequence,
                                      analysisFile,
                                      simultaneousPages))
                    }
                    threads.forEach {
                        it.setUncaughtExceptionHandler { t, e ->
                            println("Exception uncaught on thread")
                            e.printStackTrace()
                            t.interrupt()
                        }
                    }
                    threads.forEach { it.start() }
                    threads.forEach { it.join() }
                }
            }
        }

        fun getAnalysisString(track: AnalysedTrack) = getAnalysisString(track.features, track.analysis)

        fun getAnalysisString(features: AudioFeatures, analysis: AudioAnalysis): String {
            val featureString = FeatureRipper.getFeatureString(features) ?: throw IllegalArgumentException("Feature String was null")
            val analysisString = AnalysisRipper.getAnalysisString(featureString, analysis) ?: throw IllegalArgumentException("Analysis String was null")
            return analysisString
        }

        fun getAnalysisString(featureString: String, analysis: AudioAnalysis): String? {
            @Suppress("UNNECESSARY_SAFE_CALL")
            analysis?.track ?: return null

            val totalDuration = analysis.track.duration

            val segmentDuration = analysis.segments.mapNotNull { it?.measure?.duration }.average().toFloat()

            val timbre = analysis.segments
                    .asSequence()
                    .filter { it?.measure?.duration != null }
                    .filter { it?.timbre != null }
                    .map { it.measure.duration / totalDuration to it.timbre }
                    .map { (relevance, timbres) ->
                        timbres.map { timb -> timb * relevance }
                    }.plusElement(List(12) { 0f })
                    .reduce { acc, relevantTimbre ->
                        acc.zip(relevantTimbre).map { (a, b) -> a + b }
                    }

            return if (timbre.any { it != 0f }) {
                "$featureString $segmentDuration ${timbre.joinToString(" ")}\n"
            }
            else {
                println("No segment data")
                null
            }
        }
    }
}

class RetrySequence<T>(private val underlying: Iterator<T>): Sequence<T>{
    private val retryList = mutableListOf<T>()

    override fun iterator(): Iterator<T> {
        return object : Iterator<T> {
            override fun hasNext(): Boolean {
                return retryList.isNotEmpty() || underlying.hasNext()
            }

            @Synchronized
            override fun next(): T {
                return if(retryList.isEmpty()){
                    underlying.next()
                }
                else{
                    val first = retryList.first()
                    retryList.removeAt(0)
                    return first
                }
            }
        }
    }

    fun retry(value: T){
        retryList.add(value)
    }
}

class Incrementer {
    private var value = 0

    @Synchronized
    fun inc() {
        value++
    }

    fun print() {
        println("Done $value")
    }
}

class Runner(private val doneCount: Incrementer,
             private val featureStringSequence: RetrySequence<String>,
             private val analysisFile: Writer,
             private val simultaneousPages: Int) : Runnable {
    override fun run() {
        var done = false
        while (!done) {
            var sleep = false

            SpotifyAuth.manualAuth("AQAOuwxSwwmBLmFFYOx4VLURaes8e2lRdscJWLKnkvT8A5lkM95YVzIKFWayqIylXmr7m3iqdx_ND1aruS41U7wEH33S8wLSUElHllT16Ot8s_P_63WaivO9TgEybcizN3F9fg")
            val futures = (1..simultaneousPages).mapNotNull {
                //For each page
                val featureString = featureStringSequence.firstOrNull()
                if (featureString == null) {
                    done = true
                    return@mapNotNull null
                }
                else {
                    val id = featureString.id()
                    return@mapNotNull featureString to Spotify.api.getAudioAnalysisForTrack(id).build().executeAsync<AudioAnalysis>()
                }
            }

            futures
                    .asSequence()
                    .mapNotNull { (featureString, future) ->
                        try {
                            return@mapNotNull featureString to future.get()
                        }
                        catch (e: ExecutionException) {
                            val cause = e.cause
                            when (cause) {
                                is BadGatewayException, is NotFoundException -> {
                                    analysisFile.write("${featureString.id()} error\n")
                                    println("Ignoring ${featureString.id()} due to bad gateway exception / analysis not available")
                                    return@mapNotNull null
                                }
                                is TooManyRequestsException -> {
                                    //println("API Limit")
                                    featureStringSequence.retry(featureString)
                                    sleep = true
                                    return@mapNotNull null
                                }
                                else -> {
                                    e.printStackTrace()
                                    featureStringSequence.retry(featureString)
                                    return@mapNotNull null
                                }
                            }
                        }
                    }
                    .forEach { (featureString, analysis) ->
                        val line = AnalysisRipper.getAnalysisString(featureString, analysis)
                        if (line != null) {
                            analysisFile.write(line)
                            doneCount.inc()
                        }
                    }
            doneCount.print()

            if (sleep) {
                sleep = false
                Thread.sleep(1000)
            }
        }
    }
}

fun main(args: Array<String>) {
    val t1 = System.currentTimeMillis()
    val simultaneousPages = args.getOrNull(0)?.toInt() ?: 1
    AnalysisRipper.runRipper(simultaneousPages)
    val t2 = System.currentTimeMillis()
    val t = t2 - t1
    println(t.toDouble() / 1000)
}