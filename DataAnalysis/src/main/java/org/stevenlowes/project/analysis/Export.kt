package org.stevenlowes.project.analysis

import com.google.gson.Gson
import org.stevenlowes.project.analysis.app.visualisations.EdaAfterScareAroundMin
import java.io.FileWriter

fun main(args: Array<String>){
    Config.playtests.saveJson("All")

    //Config.playtests.map {
    //    it.participant
    //}.saveJson("Participants")

    //EdaAfterScareAroundMin(Config.intervention).series.saveJson("InterventionScares")
    //EdaAfterScareAroundMin(Config.control).series.saveJson("ControlScares")
}