package org.stevenlowes.project.analysis

import com.google.gson.Gson
import java.io.FileWriter

fun main(args: Array<String>){
    val gson = Gson()
    val json = gson.toJson(Config.playtests)
    FileWriter("json.txt").use {
        it.write(json)
    }

}