package com.stevenlowes.edahorror.storyteller

import com.stevenlowes.edahorror.ModController
import com.stevenlowes.edahorror.events.CreeperEvent
import net.minecraft.entity.player.EntityPlayer
import java.util.*
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min

class EDAStoryTeller(gameTimeSecs: Int) : StoryTeller(gameTimeSecs) {
    private var delay = ModController.rand.nextDouble() * (firstScareMax - firstScareMin) + firstScareMin
    private var nextScare: Double? = System.currentTimeMillis() + delay
    var scareCount = 0

    private val swings = mutableListOf<Double>()

    init {
        ModController.logger.info("Next Scare in ${delay/1000.0}s")
    }

    override fun tick(player: EntityPlayer) {
        super.tick(player)

        val t = System.currentTimeMillis()
        val scareTime = nextScare ?: return
        if (t > scareTime) {
            scareCount++
            nextScare = null
            CreeperEvent.obj.call(player)
            ModController.runAfter(measurementDelay) {
                var swing = ModController.serial.swing(measurementDelay)!!
                swings.add(swing)

                val mean = swings.average()
                val multiplierForMean = 3*exp(-3E-5 * mean)
                val adjustmentFactor = 1/multiplierForMean

                swing = max(.0, swing) //Prevent swing going negative
                val multiplier = 3*exp(-3E-5 * swing)
                val adjusted = multiplier * adjustmentFactor
                val clamped = min(max(adjusted, 0.33), 3.0)
                val delay = delay * clamped
                nextScare = System.currentTimeMillis() + delay

                ModController.logger.info("Swing: $swing")
                ModController.logger.info("Avg: $mean")
                ModController.logger.info("Multiplier: $clamped")
                ModController.logger.info("Next Scare in ${delay/1000.0}s")
            }
        }
    }

    override fun stop(player: EntityPlayer) {
        super.stop(player)
        ModController.logger.warn("$scareCount scares")
    }

    companion object {
        private const val firstScareMax = 30 * 1000
        private const val firstScareMin = 20 * 1000
        private const val measurementDelay = 5 * 1000L
    }
}
