package org.stevenlowes.tools.scraper.rippers

fun main(args: Array<String>){
    IdRipper.runRipper()
    FeatureRipper.runRipper()
    AnalysisRipper.runRipper(1)
    Filter.apply()
    PreProcessor.apply()
}