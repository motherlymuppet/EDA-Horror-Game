package org.stevenlowes.project.gui

import javafx.stage.Stage
import org.stevenlowes.project.spotifyAPI.DataContainer
import org.stevenlowes.project.spotifyAPI.SpotifyAuth
import tornadofx.*

class MainApp : App(Workspace::class) {
    override fun start(stage: Stage) {
        super.start(stage)
        DataContainer.bannedSongs.addAll(listOf(
                "4l68bFFGee2SMrLNhUGSD8",
                "4cfSDDD2HSY0QM9X3lgvjA",
                "1ej0EarRwiy2kN7HvN9ivv"
                                               ))

        stage.width = 1280.0
        stage.height = 720.0
        runAsync { SpotifyAuth.refreshAuth() }
    }

    override fun onBeforeShow(view: UIComponent) {
        workspace.dock<MainMenu>()
    }
}