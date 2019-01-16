package com.example.examplemod.events

import com.example.examplemod.MyMod
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvent
import java.util.*

class CreeperEvent() : Event {
    companion object {
        val obj = CreeperEvent()
    }

    override fun call(player: EntityPlayer) {
        MyMod.logger.info("Spawning Creeper")

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
            player.world.playSound(null,
                                   player.position,
                                   SoundEvent.REGISTRY.getObject(ResourceLocation(it.first.first))!!,
                                   SoundCategory.HOSTILE,
                                   it.first.second,
                                   it.second)
        }

        val timeMs = 1500L
        MyMod.timer.schedule(RemoveCreeperTask(), timeMs)
    }

    private class RemoveCreeperTask : TimerTask() {
        override fun run() {
            MyMod.logger.info("CreeperEvent Removed")
            CreeperOverlay.show = false
        }
    }
}