package com.stevenlowes.edahorror.setup

import com.stevenlowes.edahorror.ModController
import com.stevenlowes.edahorror.SimpleCommand
import com.stevenlowes.edahorror.events.CreeperEvent
import net.minecraft.command.ICommandSender
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.server.MinecraftServer

class CreeperCommand: SimpleCommand {
    override fun getName() = "Creeper"

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
        ModController.logger.info("Creeper Command")
        val player = sender.commandSenderEntity as EntityPlayer
        CreeperEvent.obj.call(player)
    }
}