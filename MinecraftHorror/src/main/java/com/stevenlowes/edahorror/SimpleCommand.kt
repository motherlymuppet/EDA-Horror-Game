package com.stevenlowes.edahorror

import net.minecraft.command.ICommand
import net.minecraft.command.ICommandSender
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos

interface SimpleCommand: ICommand{
    override fun getUsage(sender: ICommandSender) = "No Parameters Needed"

    override fun getTabCompletions(server: MinecraftServer,
                                   sender: ICommandSender,
                                   args: Array<String>,
                                   targetPos: BlockPos?) = mutableListOf<String>()

    override fun compareTo(other: ICommand?) = other?.name?.compareTo(name) ?: 1

    override fun checkPermission(server: MinecraftServer, sender: ICommandSender) = true

    override fun isUsernameIndex(args: Array<String>, index: Int) = false

    override fun getAliases() = mutableListOf<String>()
}