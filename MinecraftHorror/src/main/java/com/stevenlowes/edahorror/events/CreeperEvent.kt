package com.stevenlowes.edahorror.events

import com.stevenlowes.edahorror.ModController
import com.stevenlowes.edahorror.WriteDataTask
import net.minecraft.entity.player.EntityPlayer
import java.util.*

class CreeperEvent : Event {
    companion object {
        val obj = CreeperEvent()
    }

    override fun call(player: EntityPlayer) {
        ModController.logger.info("Spawning Creeper")

        CreeperOverlay.show = true

        val sounds = listOf(
                "entity.wither.spawn" to 10f to 1f,
                "block.lava.extinguish" to 10f to 0f,
                "block.glass.break" to 10f to 0f,
                "entity.horse.death" to 0.5f to 2f,
                "entity.enderdragon.growl" to 10f to 2f,
                "entity.wolf.howl" to 0.2f to 1f,
                "ambient.cave" to 10f to 1f,
                "entity.generic.explode" to 10f to 0f
                           )

        sounds.forEach {
            ModController.server.commandManager.executeCommand(
                    ModController.server,
                    "/playsound ${it.first.first} hostile @a ${player.posX} ${player.posY} ${player.posZ} ${it.first.second} ${it.second}"
                                                              )
        }

        val timeMs = 1500L
        ModController.timer.schedule(RemoveCreeperTask(), timeMs)
        ModController.eventData.addData("Creeper")
    }

    private class RemoveCreeperTask : TimerTask() {
        override fun run() {
            ModController.logger.info("CreeperEvent Removed")
            CreeperOverlay.show = false
        }
    }
}