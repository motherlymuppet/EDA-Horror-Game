package com.stevenlowes.edahorror.setup

import com.stevenlowes.edahorror.ModController
import com.stevenlowes.edahorror.SimpleCommand
import net.minecraft.command.ICommandSender
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.server.MinecraftServer

class StartCommand: SimpleCommand {
    override fun getName() = "HorrorStart"

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
        ModController.logger.info("Horror Starting")
        val player = sender.commandSenderEntity as EntityPlayer
        GameSetup.start(player)
    }
}