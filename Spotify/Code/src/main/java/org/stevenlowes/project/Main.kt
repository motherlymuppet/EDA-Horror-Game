package org.stevenlowes.project

import org.stevenlowes.project.spotifyAPI.DataContainer

fun main(args: Array<String>) {
    val container = DataContainer()
    val tagetId = container.randomId()

    (0..100).asSequence()
            .map { container.closestToDistance(it.toFloat(), tagetId) }
            .map { container.distance(tagetId, it) }
            .withIndex()
            .forEach { (index, value) ->
                println("$index : $value")
            }
}