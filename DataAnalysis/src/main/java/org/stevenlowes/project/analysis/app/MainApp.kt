package org.stevenlowes.project.analysis.app

import javafx.application.Application
import javafx.stage.Stage
import tornadofx.App
import tornadofx.UIComponent
import tornadofx.Workspace
import kotlin.reflect.KClass

class MainApp : App(Workspace::class) {
    override fun start(stage: Stage) {
        super.start(stage)

        stage.width = 1280.0
        stage.height = 720.0
    }

    override val primaryView: KClass<out UIComponent>
        get() = MainMenu::class
}

fun main(args: Array<String>){
    Application.launch(MainApp::class.java, *args)
}