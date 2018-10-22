package org.stevenlowes.project.serialreader

import javafx.application.Application
import javafx.application.Application.launch
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.scene.Scene
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import javafx.util.StringConverter
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javafx.animation.AnimationTimer
import javafx.scene.control.Slider
import javafx.scene.layout.BorderPane

class Grapher : Application(){
    private val zoneOffset = ZoneOffset.ofHours(-1)
    private val data = FXCollections.observableArrayList<XYChart.Data<Number, Number>>()
    private val buffer = mutableListOf<XYChart.Data<Number, Number>>()

    private val xAxis = NumberAxis()
    private val yAxis = NumberAxis()

    private var xSize = 60.0

    private fun buffer(time: Long, serialValue: Int){
        buffer.add(XYChart.Data(time, serialValue))
    }

    private fun emptyBuffer(){
        buffer.asSequence().filter { it.yValue.toDouble() > 1.0 }.forEach { data.add(it) }
        buffer.clear()
        xAxis.upperBound = System.currentTimeMillis().toDouble() //TODO this is a mess
        xAxis.lowerBound = Math.min(xAxis.lowerBound, data.getOrNull(0)?.xValue?.toDouble() ?: 0.0)
        xAxis.lowerBound = Math.max(xAxis.lowerBound, xAxis.upperBound - xSize*1000)

        //data.removeIf {it.xValue.toDouble() < xAxis.lowerBound}

        val inRange = data.filter { it.xValue.toDouble() >= xAxis.lowerBound && it.xValue.toDouble() <= xAxis.upperBound }

        if(inRange.isNotEmpty()){
            yAxis.lowerBound = Math.max(inRange.asSequence().map {it.yValue.toDouble()}.min()!! - 128, 0.0)
            yAxis.upperBound = Math.min(inRange.asSequence().map {it.yValue.toDouble()}.max()!! + 128, 10240.0)
        }
    }

    override fun start(primaryStage: Stage) {
        val root = StackPane()


        xAxis.label = "Time"
        xAxis.isAutoRanging = false
        xAxis.lowerBound = System.currentTimeMillis().toDouble()
        xAxis.tickUnit = (xSize*200).toDouble()
        xAxis.tickLabelFormatter = object: StringConverter<Number>(){
            val formatter = DateTimeFormatter.ISO_DATE_TIME

            override fun toString(obj: Number): String {
                val dateTime = LocalDateTime.ofEpochSecond(obj.toLong()/1000, 0, zoneOffset)
                return dateTime.toString()
            }

            override fun fromString(dateTimeString: String?): Number {
                if(dateTimeString == null){
                    return -1
                }
                val dateTime = LocalDateTime.parse(dateTimeString, formatter)
                return dateTime.toEpochSecond(zoneOffset)*1000
            }
        }

        yAxis.label = "Relaxation"
        yAxis.tickUnit = 64.0
        yAxis.lowerBound = 0.0
        yAxis.upperBound = 10240.0
        yAxis.isAutoRanging = false

        val series = XYChart.Series("Relaxation over Time", data)
        val serieses = FXCollections.observableArrayList(series)
        val chart = LineChart(xAxis, yAxis, serieses)
        chart.createSymbols = false
        chart.animated = false

        val slider = Slider(1.0, 300.0, xSize)
        slider.valueProperty().addListener { _, _, newValue -> xSize = newValue.toDouble() }
        val pane = BorderPane(chart, null, null, slider, null)
        root.children.add(pane)

        val scene = Scene(root, 1280.0, 720.0)
        primaryStage.scene = scene
        primaryStage.show()

        Serial(4) {
            Platform.runLater {
                buffer(System.currentTimeMillis(), it)
            }
        }

        prepareTimeline()
    }

    private fun prepareTimeline() {
        // Every frame to take any data from queue and add to chart
        object : AnimationTimer() {
            override fun handle(now: Long) {
                emptyBuffer()
            }
        }.start()
    }
}

fun main(args: Array<String>){
    launch(Grapher::class.java, *args)
}