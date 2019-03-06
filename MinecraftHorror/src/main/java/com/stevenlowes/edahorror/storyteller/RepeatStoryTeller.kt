package com.stevenlowes.edahorror.storyteller

import com.google.gson.JsonParser
import com.stevenlowes.edahorror.ModController
import com.stevenlowes.edahorror.events.CreeperEvent
import net.minecraft.entity.player.EntityPlayer
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class RepeatStoryTeller(repeatOf: File, gameTimeSecs: Int) : StoryTeller(gameTimeSecs) {
    val startTime = System.currentTimeMillis()

    val scareTimes = JsonParser()
            .parse(BufferedReader(FileReader(repeatOf)))
            .run {
                val events = asJsonObject["Event Data"].asJsonArray
                        .map { element ->
                            val obj = element.asJsonObject
                            return@map obj["time"].asLong to obj["event"].asString
                        }
                val previousStartTime = events.first { it.second == "Start" }.first
                val offset = startTime - previousStartTime
                return@run events.filter { it.second == "Creeper" }.map { it.first + offset }
            }.toMutableList()

    init {
        ModController.logger.info(scareTimes.map { it - startTime }.joinToString(","))
    }

    override fun tick(player: EntityPlayer) {
        super.tick(player)

        val t = System.currentTimeMillis()
        if (scareTimes.removeIf { t > it }) {
            CreeperEvent.obj.call(player)
        }
    }
}