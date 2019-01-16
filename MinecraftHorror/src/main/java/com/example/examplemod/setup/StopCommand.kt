package com.example.examplemod.setup

import com.example.examplemod.MyMod
import com.example.examplemod.SimpleCommand
import net.minecraft.command.ICommandSender
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.server.MinecraftServer

class StopCommand: SimpleCommand{
    override fun getName() = "HorrorStop"

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
        MyMod.logger.info("Horror Stopping")
        val player = sender.commandSenderEntity as EntityPlayer
        GameSetup.stop(player)
    }
}