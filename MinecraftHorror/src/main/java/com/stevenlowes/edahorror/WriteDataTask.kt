package com.stevenlowes.edahorror

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object WriteDataTask {
    private const val delayMs = 5000L

    fun run(eventName: String) {
        ModController.runAfter(delayMs) {
            ModController.logger.info("Writing GSR Data")

            val currentMillis = System.currentTimeMillis()
            val getDataSince = currentMillis - delayMs

            val gsrData = ModController.serial.data.filter { it.first >= getDataSince }.map {(time, value) ->
                val dataPoint = JsonObject()
                dataPoint.addProperty("time", time)
                dataPoint.addProperty("value", value)
                return@map dataPoint
            }.fold(JsonArray()){array, dataPoint ->
                array.add(dataPoint)
                return@fold array
            }

            val mouseData = ModController.mouse.data.filter { it.first >= getDataSince }.map { (time, angle) ->
                val dataPoint = JsonObject()
                dataPoint.addProperty("time", time)
                dataPoint.addProperty("pitch", angle.pitch)
                dataPoint.addProperty("yaw", angle.yaw)
                return@map dataPoint
            }.fold(JsonArray()){array, dataPoint ->
                array.add(dataPoint)
                return@fold array
            }

            val json = JsonObject()
            json.addProperty("Event", eventName)
            json.addProperty("Time", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            json.add("GSR", gsrData)
            json.add("Mouse", mouseData)


            val file = File("$eventName-${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))}.txt")

            file.printWriter().use {
                it.write(json.toString())
            }

            ModController.logger.info("Done Writing GSR Data")
        }
    }
}