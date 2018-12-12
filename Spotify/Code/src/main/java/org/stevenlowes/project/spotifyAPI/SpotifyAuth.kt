package org.stevenlowes.project.spotifyAPI
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import com.wrapper.spotify.SpotifyHttpManager
import java.net.InetSocketAddress
import java.nio.charset.Charset

class SpotifyAuth {
    companion object {
        internal val clientId = "f9960a1f12f04266be8c080100a137a6"
        internal val clientSecret = "3dacf507e5464386a4528eff4945a710"
        internal val redirectUri = SpotifyHttpManager.makeUri("http://localhost:46948")

        private var authorised = false

        init {
            val api = Spotify.api
            api.refreshToken = "AQBw35jIyp69ZwpBcz98U22ZbUiPsbVukR6EHgM3oFKSmrZLcTgqmQHAPR9IusYbPAu9ToFo-pBQLXqcRpUu5vcZGY4Jl8nBsb2UJ0RG8SQvjNrzS44RxdZdYoYuG7mhtAcHBA"
        }

        private fun printAuthURI() {
            val authURI = Spotify.api.authorizationCodeUri().show_dialog(true).scope(
                    listOf("playlist-read-private",
                           "playlist-modify-public",
                           "playlist-modify-private",
                           "playlist-read-collaborative",
                           "user-read-private",
                           "user-read-birthdate",
                           "user-read-email",
                           "user-read-playback-state",
                           "user-read-currently-playing",
                           "user-modify-playback-state",
                           "app-remote-control",
                           "streaming",
                           "user-follow-modify",
                           "user-follow-read",
                           "user-top-read",
                           "user-read-recently-played",
                           "user-library-read",
                           "user-library-modify"
                          ).joinToString()).build().execute()

            println(authURI)
        }

        private fun waitForAuth() {
            //TODO actually use wait()
            while (!authorised) {
                Thread.sleep(100)
            }
        }

        fun newAuth() {
            CredentialsListener { code ->
                val credentials = Spotify.api.authorizationCode(code).build().execute()
                Spotify.api.accessToken = credentials.accessToken
                Spotify.api.refreshToken = credentials.refreshToken

                println("Refresh Token")
                println(Spotify.api.refreshToken)

                authorised = true
            }
            printAuthURI()
            waitForAuth()
        }

        fun refreshAuth() {
            val credentials = Spotify.api.authorizationCodeRefresh().build().execute()
            Spotify.api.accessToken = credentials.accessToken
            authorised = true
        }

        fun manualAuth(refreshToken: String) {
            Spotify.api.refreshToken = refreshToken
            refreshAuth()
        }
    }
}

private class CredentialsListener(listener: (String) -> Unit){
    private val server = HttpServer.create(InetSocketAddress(46948), 0)

    init {
        server.createContext("/", CredentialsHandler({server.stop(0)}, listener))
        server.executor = null
        server.start()
    }
}

private class CredentialsHandler(val stop: () -> Unit, val listener: (String) -> Unit): HttpHandler {
    override fun handle(exchange: HttpExchange?) {
        if(exchange == null){
            return
        }

        val code = exchange.requestURI.query.substring(5)

        val response = "Success!".toByteArray(Charset.defaultCharset())
        exchange.sendResponseHeaders(200, response.size.toLong())
        val responseBody = exchange.responseBody
        responseBody.write("Success!".toByteArray(Charset.defaultCharset()))
        exchange.close()
        println("Response Sent")

        listener(code)
        println("Code Authorised")

        stop()
        println("Server Stopped")
    }
}