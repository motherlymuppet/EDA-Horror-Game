package org.stevenlowes.project.gui

import org.stevenlowes.project.spotifyAPI.SpotifyAuth
import tornadofx.*

class MainApp: App(MainMenu::class){
    init {
        SpotifyAuth.refreshAuth()
    }
}