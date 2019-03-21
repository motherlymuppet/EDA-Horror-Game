package org.stevenlowes.project.analysis.data

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.stevenlowes.project.analysis.Series

data class Playtest(
    val participant: Participant,
    val storyteller: Storyteller,
    val lengthMs: Long,
    val mouseData: Map<Long, Angle>,
    val edaData: Map<Long, Double>,
    val scares: List<Long>
) {
    constructor(participant: Participant, json: JsonElement) : this(
        participant,
        json.asJsonObject
    )

    private constructor(participant: Participant, json: JsonObject) : this(
        participant,
        json["StoryTeller"].asString,
        json["Mouse Data"].asJsonArray,
        json["GSR"].asJsonArray,
        json["Event Data"].asJsonArray
    )

    private constructor(
        participant: Participant,
        storyTeller: String,
        mouseData: JsonArray,
        edaData: JsonArray,
        eventData: JsonArray
    ) : this(
        participant,
        storyTeller,
        mouseData,
        edaData,
        eventData,
        eventData.first().asJsonObject["time"].asLong
    )

    private constructor(
        participant: Participant,
        storyTeller: String,
        mouseData: JsonArray,
        edaData: JsonArray,
        eventData: JsonArray,
        timeOffset: Long
    ) : this(
        participant,

        //Storyteller
        Storyteller.from(storyTeller),

        //End time
        eventData.last().asJsonObject["time"].asLong - timeOffset,

        //Mouse Data
        mouseData.asSequence().map {
            val json = it.asJsonObject

            val epochTime = json["time"].asLong
            val offsetTime = epochTime - timeOffset

            val pitch = json["pitch"].asDouble
            val yaw = json["yaw"].asDouble
            val angle = Angle(pitch, yaw)

            return@map offsetTime to angle
        }.toMap(),

        //Eda Data
        edaData.asSequence().map {
            val json = it.asJsonObject

            val epochTime = json["time"].asLong
            val offsetTime = epochTime - timeOffset

            val edaValue = json["value"].asDouble

            return@map offsetTime to edaValue
        }.toMap(),

        //Scares
        eventData.asSequence().map {
            val json = it.asJsonObject

            val epochTime = json["time"].asLong
            val offsetTime = epochTime - timeOffset

            val event = json["event"].asString

            return@map offsetTime to event
        }
            .filter { it.second == "Creeper" }
            .map { it.first }
            .distinct()
            //.filter { it < eventData.last().asJsonObject["time"].asLong - timeOffset - 10_000 } //TODO remove
            .toList()
    )
}