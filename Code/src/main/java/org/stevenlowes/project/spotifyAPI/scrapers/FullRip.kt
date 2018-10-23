package org.stevenlowes.project.spotifyAPI.scrapers

fun main(args: Array<String>){
    IdRipper.runRipper()
    FeatureRipper.runRipper()
    AnalysisRipper.runRipper(1)
    Filter.apply()
    StdDevCalculator.calculate()
    PreProcessor.translateFile()
}