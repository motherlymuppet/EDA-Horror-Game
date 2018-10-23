package org.stevenlowes.project

import javafx.application.Application
import javafx.application.Application.launch
import javafx.stage.Stage

class Main : Application(){
    override fun start(primaryStage: Stage?) {
        main(arrayOf())
    }
}

fun main(args: Array<String>){
    launch(Main::class.java)
}