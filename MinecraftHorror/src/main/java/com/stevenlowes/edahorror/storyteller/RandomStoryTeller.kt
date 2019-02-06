package com.stevenlowes.edahorror.storyteller

import com.stevenlowes.edahorror.ModController
import com.stevenlowes.edahorror.events.CreeperEvent
import net.minecraft.entity.player.EntityPlayer
import org.apache.logging.log4j.Level

class RandomStoryTeller(private val scareCount: Int, gameTimeSecs: Int) : StoryTeller(gameTimeSecs) {
    val startTime = System.currentTimeMillis()
    val delayBetween = (gameTimeMillis - shiftTimeMillis) / scareCount
    private val scareTimes = (1..scareCount)
            .map { it * delayBetween }
            .map { it + ModController.rand.nextInt(shiftTimeMillis) - 0.5 * shiftTimeMillis }
            .map { it + startTime }
            .sorted()
            .toMutableList()

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

    companion object {
        private const val shiftTimeMillis = 10 * 1000
    }
}