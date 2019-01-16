package com.example.examplemod

import com.example.examplemod.MyMod.MODID
import com.example.examplemod.MyMod.NAME
import com.example.examplemod.MyMod.VERSION
import com.example.examplemod.events.Lightning
import com.example.examplemod.events.TestCommands
import com.example.examplemod.serial.Serial
import com.example.examplemod.setup.CreeperCommand
import com.example.examplemod.setup.GameSetup
import com.example.examplemod.setup.StartCommand
import com.example.examplemod.setup.StopCommand
import gnu.io.CommPortIdentifier
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerStartingEvent
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.apache.logging.log4j.Logger
import java.util.*

@Mod.EventBusSubscriber(modid = MODID)
@Mod(modid = MODID, name = NAME, version = VERSION, modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter")
object MyMod {
    const val MODID = "edahorror"
    const val NAME = "EDA Horror MyMod"
    const val VERSION = "1.0.0"
    val timer = Timer()

    lateinit var logger: Logger
        private set
    lateinit var serial: Serial
        private set

    @EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        logger = event.modLog
    }

    @EventHandler
    fun init(event: FMLInitializationEvent) {
        serial = Serial(CommPortIdentifier.getPortIdentifier("COM4"), 120)
        Runtime.getRuntime().addShutdownHook(Thread(Runnable { serial.close() }))
    }

    @EventHandler
    fun serverLoad(event: FMLServerStartingEvent){
        event.registerServerCommand(StartCommand())
        event.registerServerCommand(StopCommand())
        event.registerServerCommand(CreeperCommand())
        TestCommands.eventCommands.forEach {
            event.registerServerCommand(it)
        }
    }

    @EventHandler
    fun serverUnload(event: FMLServerStoppingEvent){
        val player: EntityPlayer = Minecraft.getMinecraft().player
        GameSetup.stop(player)
    }

    private var dropping = false

    @SubscribeEvent
    @JvmStatic
    fun onPlayerTick(event: TickEvent.PlayerTickEvent) {
        val player = event.player

        val gradient = serial.gradient(500) ?: return

        if(gradient < -1.5 && !dropping){
            dropping = true
            Lightning.obj.call(player)
        }
        else if(gradient > 0.5 && dropping){
            dropping = false
        }
    }
}
