package com.example.examplemod.events

import com.example.examplemod.MyMod
import net.minecraft.entity.effect.EntityLightningBolt
import net.minecraft.entity.player.EntityPlayer
import java.util.*


class Lightning private constructor() : Event {
    private val delayPer: Long = 100

    companion object {
        val obj = Lightning()
    }

    override fun call(player: EntityPlayer) {
        (1..5).forEach {
            MyMod.timer.schedule(LightningTask(player), delayPer * it)
        }
    }

    private class LightningTask(private val player: EntityPlayer): TimerTask(){
        override fun run() {
            player.world.addWeatherEffect(EntityLightningBolt(player.world, player.posX, player.posY, player.posZ, true))
        }
    }
}