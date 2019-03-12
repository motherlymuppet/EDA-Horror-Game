package org.stevenlowes.project.analysis.data

import java.io.File

data class ParticipantRegistry(val participants: Map<Int, Participant>) {
    constructor(participants: File) : this(
        participants.readLines().drop(1).map {
            val split = it.split(",")
            val id = split[0].toInt()
            val scaredness = split[1].toInt()

            val pairStr = split[2]
            val pair = if(pairStr.isEmpty()){
                null
            }
            else{
                pairStr.toInt()
            }

            val enjoyment = split[3].toInt()
            val timing = split[4].toInt()

            return@map id to Participant(
                id,
                scaredness,
                pair,
                enjoyment,
                timing
            )
        }.toMap()
    )

    operator fun get(key: Int) = participants[key]
}