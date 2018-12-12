package com.example.examplemod.setup

import com.example.examplemod.Mod
import com.example.examplemod.SimpleCommand
import net.minecraft.command.ICommand
import net.minecraft.command.ICommandSender
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos

class StartCommand: SimpleCommand{
    override fun getName() = "HorrorStart"

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
        Mod.logger.info("Horror Starting")
        val player = sender.commandSenderEntity as EntityPlayer
        GameSetup.start(player)
    }
}