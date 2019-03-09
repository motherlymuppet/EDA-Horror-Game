package com.stevenlowes.edahorror.storyteller

import com.stevenlowes.edahorror.ModController
import com.stevenlowes.edahorror.events.CreeperEvent
import net.minecraft.entity.player.EntityPlayer
import java.util.*
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min

class EDAStoryTeller(gameTimeSecs: Int) : StoryTeller(gameTimeSecs) {
    private var stage = 0
    private var delay: Double = randomDelay()
    private var nextScare: Double? = System.currentTimeMillis() + delay
    private val swings = mutableListOf<Double>()

    override fun tick(player: EntityPlayer) {
        super.tick(player)

        val t = System.currentTimeMillis()
        val scareTime = nextScare ?: return

        if (t > scareTime) {
            nextScare = null
            CreeperEvent.obj.call(player)

            ModController.runAfter(measurementDelay) {
                val swing = ModController.serial.swing(measurementDelay)!!
                ModController.logger.info("Swing: $swing")
                swings.add(swing)

                if(stage < 3){
                    stage++
                    delay = randomDelay()
                    ModController.logger.info("Next Scare in ${delay/1000.0}s")
                    nextScare = System.currentTimeMillis() + delay
                }
                else{
                    val mean = swings.average()
                    val stdDev = swings.stdDevs()

                    val stdDevsAboveMean = (swing - mean)/stdDev
                    val multiplier = Math.exp(-0.366 * stdDevsAboveMean)
                    val clamped = min(max(multiplier, 0.33), 3.0)
                    delay = min(delay * clamped, 100.0 * 1000)
                    nextScare = System.currentTimeMillis() + delay

                    ModController.logger.info("Avg: $mean")
                    ModController.logger.info("StdDev: $stdDev")
                    ModController.logger.info("StdDevsAboveMean: $stdDevsAboveMean")
                    ModController.logger.info("Multiplier: $clamped")
                    ModController.logger.info("Next Scare in ${delay/1000.0}s")
                }
            }
        }
    }

    companion object {
        private const val measurementDelay = 5 * 1000L
    }

    private fun randomDelay() : Double = (20 + ModController.rand.nextDouble() * 10) * 1000
}

private fun List<Double>.stdDevs(): Double{
    val mean = average()
    val sd = fold(0.0) { accumulator, next -> accumulator + (next - mean).square() }
    return Math.sqrt(sd / (size-1))
}

private fun Double.square() = this * this
