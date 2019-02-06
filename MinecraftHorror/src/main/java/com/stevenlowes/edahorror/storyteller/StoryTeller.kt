package com.stevenlowes.edahorror.storyteller

import com.stevenlowes.edahorror.ModController
import com.stevenlowes.edahorror.setup.GameSetup
import net.minecraft.client.Minecraft
import net.minecraft.command.CommandTitle
import net.minecraft.command.server.CommandSetBlock
import net.minecraft.entity.player.EntityPlayer
import java.util.*

abstract class StoryTeller(gameTimeSecs: Int) {
    val gameTimeMillis = gameTimeSecs * 1000L
    private val stopTime = System.currentTimeMillis() + gameTimeMillis
    private var playing = true

    open fun tick(player: EntityPlayer){
        if(playing && System.currentTimeMillis() > stopTime){
            stop(player)
        }
    }

    open fun stop(player: EntityPlayer) {
        ModController.server.commandManager
                .executeCommand(ModController.server, "/title @a title {\"text\":\"Test Complete\",\"color\":\"white\"}")
        playing = false
        GameSetup.stop(player)
        ModController.logger.info("Playthrough done")
    }
}