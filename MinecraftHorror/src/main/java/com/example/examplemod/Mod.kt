package com.example.examplemod

import com.example.examplemod.Mod.MODID
import com.example.examplemod.Mod.NAME
import com.example.examplemod.Mod.VERSION
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.Logger

@Mod(modid = MODID, name = NAME, version = VERSION, modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter")
object Mod {

    const val MODID = "edahorror"
    const val NAME = "EDA Horror Mod"
    const val VERSION = "1.0.0"

    private lateinit var logger: Logger

    @EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        logger = event.modLog
    }

    @EventHandler
    fun init(event: FMLInitializationEvent) {

    }
}
