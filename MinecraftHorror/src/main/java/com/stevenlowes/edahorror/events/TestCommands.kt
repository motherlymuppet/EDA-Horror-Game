package com.stevenlowes.edahorror.events

import com.stevenlowes.edahorror.SimpleCommand
import net.minecraft.command.ICommandSender
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.server.MinecraftServer

object TestCommands{
    val eventCommands = listOf(BlindCommand(),
                               LightningCommand())

    private class BlindCommand: SimpleCommand {
        override fun getName() = "blind"

        override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
            Blind.long.call(sender.commandSenderEntity as EntityPlayer)
        }
    }

    private class LightningCommand: SimpleCommand {
        override fun getName() = "lightning"

        override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
            Lightning.obj.call(sender.commandSenderEntity as EntityPlayer)
        }
    }
}