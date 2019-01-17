package com.stevenlowes.edahorror.events

import net.minecraft.entity.player.EntityPlayer

interface Event{
    fun call(player: EntityPlayer)
}