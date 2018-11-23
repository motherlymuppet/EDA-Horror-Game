package org.stevenlowes.project.gui

import javafx.stage.Stage
import org.stevenlowes.project.spotifyAPI.SpotifyAuth
import tornadofx.*

class MainApp : App(Workspace::class) {
    override fun start(stage: Stage) {
        super.start(stage)
        stage.width = 1280.0
        stage.height = 720.0
        runAsync { SpotifyAuth.refreshAuth() }
    }

    override fun onBeforeShow(view: UIComponent) {
        workspace.dock<MainMenu>()
    }
}