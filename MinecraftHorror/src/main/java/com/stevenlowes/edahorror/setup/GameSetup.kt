package com.stevenlowes.edahorror.setup

import com.stevenlowes.edahorror.ModController
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionEffect
import net.minecraft.util.NonNullList
import net.minecraft.world.EnumDifficulty

object GameSetup {
    private var enabled = false

    fun start(player: EntityPlayer) {
        if (!enabled) {
            enabled = true
            ModController.logger.info("Horror Start")
            blind(player)
            slow(player)
            Minecraft.getMinecraft().gameSettings.saveOptions()
            useGameSettings(player)
        }
    }

    private val inventory = NonNullList.create<ItemStack>()

    private var prevHealth: Float = 0f
    private var prevFood: Int = 0
    private var prevSaturation: Float = 0f

    private fun useGameSettings(player: EntityPlayer) {
        val settings = Minecraft.getMinecraft().gameSettings
        settings.difficulty = EnumDifficulty.PEACEFUL
        settings.renderDistanceChunks = 2
        settings.limitFramerate = 240
        settings.fovSetting = 90f
        settings.autoJump = false
        settings.keyBindInventory.keyCode = 0
        settings.keyBindDrop.keyCode = 0
        settings.keyBindSprint.keyCode = 0
        settings.gammaSetting = 0f

        inventory.clear()
        inventory.addAll(player.inventory.mainInventory)

        (9..35).forEach {
            player.inventory.mainInventory[it] = ItemStack(Item.getItemById(166), 1)
        }

        prevHealth = player.health
        player.health = 20f

        val foodStats = player.foodStats

        prevFood = foodStats.foodLevel
        prevSaturation = foodStats.saturationLevel
        foodStats.foodLevel = 20
        foodStats.setFoodSaturationLevel(100000f)
    }

    fun stop(player: EntityPlayer) {
        if (enabled) {
            enabled = false
            ModController.logger.info("Horror End")
            removePotions(player)
            revertGameSettings(player)
        }
    }



    private fun revertGameSettings(player: EntityPlayer) {
        Minecraft.getMinecraft().gameSettings.loadOptions()

        player.inventory.mainInventory.clear()
        (0..35).forEach {
            player.replaceItemInInventory(it, inventory[it])
        }

        player.health = prevHealth
        val foodStats = player.foodStats
        foodStats.foodLevel = prevFood
        foodStats.setFoodSaturationLevel(prevSaturation)
    }

    private fun blind(player: EntityPlayer) {
        val potion = Potion.getPotionById(15)!!
        val potionEffect = PotionEffect(potion, Int.MAX_VALUE, -2, true, false)
        player.addPotionEffect(potionEffect)
    }

    private fun slow(player: EntityPlayer) {
        val potion = Potion.getPotionById(2)!!
        val potionEffect = PotionEffect(potion, Int.MAX_VALUE, 1, true, false)
        player.addPotionEffect(potionEffect)
    }

    private fun removePotions(player: EntityPlayer) {
        while(player.activePotionEffects.isNotEmpty()){
            player.removePotionEffect(player.activePotionEffects.first().potion)
        }
    }
}