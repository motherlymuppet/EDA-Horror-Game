package com.stevenlowes.edahorror

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.stevenlowes.edahorror.storyteller.EDAStoryTeller
import com.stevenlowes.edahorror.storyteller.RepeatStoryTeller
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object WriteDataTask {
    private const val delayMs = 5000L

    fun run() {
        ModController.runAfter(delayMs) {
            ModController.logger.info("Writing GSR Data")

            val gsrData = ModController.serial.data.map { (time, value) ->
                val dataPoint = JsonObject()
                dataPoint.addProperty("time", time)
                dataPoint.addProperty("value", value)
                return@map dataPoint
            }.fold(JsonArray()){array, dataPoint ->
                array.add(dataPoint)
                return@fold array
            }

            val mouseData = ModController.MOUSE_DATA.data.map { (time, angle) ->
                val dataPoint = JsonObject()
                dataPoint.addProperty("time", time)
                dataPoint.addProperty("pitch", angle.pitch)
                dataPoint.addProperty("yaw", angle.yaw)
                return@map dataPoint
            }.fold(JsonArray()){array, dataPoint ->
                array.add(dataPoint)
                return@fold array
            }

            val eventData = ModController.eventData.data.map{ (time, event) ->
                val dataPoint = JsonObject()
                dataPoint.addProperty("time", time)
                dataPoint.addProperty("event", event)
                return@map dataPoint
            }.fold(JsonArray()){array, dataPoint ->
                array.add(dataPoint)
                return@fold array
            }

            val json = JsonObject()
            json.addProperty("Time", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            json.add("GSR", gsrData)
            json.add("Mouse Data", mouseData)
            json.add("Event Data", eventData)
            json.addProperty("StoryTeller", ModController.storyTeller.toString())


            val file = File("${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))}.txt")

            file.printWriter().use {
                it.write(json.toString())
            }

            ModController.logger.info("Done Writing GSR Data")
        }
    }
}