package org.stevenlowes.tools.scraper.rippers

import com.neovisionaries.i18n.CountryCode
import com.wrapper.spotify.exceptions.detailed.NotFoundException
import com.wrapper.spotify.model_objects.specification.Paging
import com.wrapper.spotify.model_objects.specification.Track
import org.stevenlowes.tools.scraper.Spotify
import org.stevenlowes.tools.scraper.SpotifyAuth
import java.io.FileWriter
import java.util.concurrent.ExecutionException

class IdRipper {
    companion object {
        fun runRipper(){
            ('a'..'z').forEach { firstChar ->
                SpotifyAuth.manualAuth("AQAOuwxSwwmBLmFFYOx4VLURaes8e2lRdscJWLKnkvT8A5lkM95YVzIKFWayqIylXmr7m3iqdx_ND1aruS41U7wEH33S8wLSUElHllT16Ot8s_P_63WaivO9TgEybcizN3F9fg")
                ('a'..'z').forEach { secondChar ->
                    val searchTerm = firstChar.toString() + secondChar.toString()
                    println(searchTerm)
                    IdRipper.getAllIDs(searchTerm)
                }
            }
        }

        private fun getAllIDs(searchTerm: String) {
            FileWriter("data.txt").use { fileWriter ->
                val dataSet = HashSet<String>()
                val limit = 50
                val simulataneousFetches = 125
                val pages = (0..(100 * 1000 / limit)).toMutableList()
                var done = false

                while (pages.isNotEmpty() && !done) {
                    val usedPages = pages.take(simulataneousFetches).sorted()
                    pages.removeAll(usedPages)

                    println("${usedPages.first()},${usedPages.last()}")


                    val futures = usedPages.map {
                        it to Spotify.api.searchTracks(searchTerm).limit(limit).market(CountryCode.GB).offset(it * limit).build().executeAsync<Paging<Track>>()
                    }

                    futures.asSequence().mapNotNull { (id, future) ->
                        try {
                            return@mapNotNull future.get().items.toList()
                        }
                        catch (e: ExecutionException) {
                            val cause = e.cause
                            when (cause) {
                                is NotFoundException -> done = true
                                else -> {
                                    pages.add(0, id)
                                }
                            }
                            return@mapNotNull null
                        }
                    }
                            .flatten()
                            .map { it.id }
                            .filterNotNull()
                            .forEach {id ->
                                if (!dataSet.contains(id)) {
                                    dataSet.add(id)
                                    fileWriter.write("$id\n")
                                }
                            }

                    Thread.sleep(5000)
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    IdRipper.runRipper()
}