package com.example.examplemod.setup

import com.example.examplemod.MyMod
import com.example.examplemod.SimpleCommand
import com.example.examplemod.events.CreeperEvent
import net.minecraft.command.ICommandSender
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.server.MinecraftServer

class CreeperCommand: SimpleCommand{
    override fun getName() = "Creeper"

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
        MyMod.logger.info("Creeper Command")
        val player = sender.commandSenderEntity as EntityPlayer
        CreeperEvent.obj.call(player)
    }
}