package com.example.examplemod.setup

import com.example.examplemod.MyMod
import com.example.examplemod.SimpleCommand
import net.minecraft.command.ICommandSender
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.server.MinecraftServer

class StartCommand: SimpleCommand{
    override fun getName() = "HorrorStart"

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
        MyMod.logger.info("Horror Starting")
        val player = sender.commandSenderEntity as EntityPlayer
        GameSetup.start(player)
    }
}