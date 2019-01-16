package com.example.examplemod.events

import com.example.examplemod.MyMod
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.GameSettings
import net.minecraft.entity.player.EntityPlayer
import java.util.*

class Blind(private val timeMs: Long): Event{
    companion object {
        val short = Blind(250)
        val medium = Blind(500)
        val long = Blind(750)
    }

    //TODO this should play a sound too

    override fun call(player: EntityPlayer) {
        MyMod.logger.info("Blinding")
        val options = Minecraft.getMinecraft().gameSettings
        val originalGamma = options.gammaSetting

        options.gammaSetting = Float.NEGATIVE_INFINITY
        MyMod.timer.schedule(BlindnessTask(options, originalGamma), timeMs)
    }

    private class BlindnessTask(val options: GameSettings, val originalGamma: Float): TimerTask(){
        override fun run() {
            MyMod.logger.info("Un-Blinding")
            options.gammaSetting = originalGamma
        }
    }
}