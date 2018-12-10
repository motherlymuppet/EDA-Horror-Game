package com.example.examplemod

import com.example.examplemod.ExampleMod.MODID
import com.example.examplemod.ExampleMod.NAME
import com.example.examplemod.ExampleMod.VERSION
import net.minecraft.init.Blocks
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.Logger

@Mod(modid = MODID, name = NAME, version = VERSION, modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter")
object ExampleMod {

    const val MODID = "examplemod"
    const val NAME = "Example Mod"
    const val VERSION = "1.0"

    private lateinit var logger: Logger

    @EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        logger = event.modLog
    }

    @EventHandler
    fun init(event: FMLInitializationEvent) {
        logger.info("DIRT BLOCK >> {}", Blocks.DIRT.registryName)
    }
}
