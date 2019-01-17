package com.stevenlowes.edahorror.events

import com.stevenlowes.edahorror.ModController
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.SoundEvent

class PlaySound(private val sound: SoundEvent, private val volume: Float): Event {
    override fun call(player: EntityPlayer) {
        ModController.logger.info("Playing Sound")
        player.playSound(sound, volume, 1f)
    }
}