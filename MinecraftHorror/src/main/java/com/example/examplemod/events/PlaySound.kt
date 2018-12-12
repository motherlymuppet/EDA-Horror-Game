package com.example.examplemod.events

import com.example.examplemod.Mod
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.SoundEvent

class PlaySound(private val sound: SoundEvent, private val volume: Float): Event{
    override fun call(player: EntityPlayer) {
        Mod.logger.info("Playing Sound")
        player.playSound(sound, volume, 1f)
    }
}