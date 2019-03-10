package com.stevenlowes.edahorror

import com.stevenlowes.edahorror.ModController.MODID
import com.stevenlowes.edahorror.ModController.NAME
import com.stevenlowes.edahorror.ModController.VERSION
import com.stevenlowes.edahorror.data.EventData
import com.stevenlowes.edahorror.data.MouseData
import com.stevenlowes.edahorror.data.Serial
import com.stevenlowes.edahorror.events.TestCommands
import com.stevenlowes.edahorror.setup.CreeperCommand
import com.stevenlowes.edahorror.setup.GameSetup
import com.stevenlowes.edahorror.setup.StartCommand
import com.stevenlowes.edahorror.setup.StopCommand
import com.stevenlowes.edahorror.storyteller.EDAStoryTeller
import com.stevenlowes.edahorror.storyteller.RepeatStoryTeller
import com.stevenlowes.edahorror.storyteller.StoryTeller
import com.stevenlowes.edahorror.storyteller.StoryTellerType
import gnu.io.CommPortIdentifier
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionEffect
import net.minecraft.server.MinecraftServer
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerStartingEvent
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.apache.commons.io.FileUtils
import org.apache.logging.log4j.Logger
import java.io.File
import java.util.*

@Mod.EventBusSubscriber(modid = MODID)
@Mod(modid = MODID, name = NAME, version = VERSION, modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter")
object ModController {
    const val MODID = "edahorror"
    const val NAME = "EDA Horror ModController"
    const val VERSION = "1.0.0"
    val timer = Timer()
    val rand = Random()
    lateinit var storyTeller: StoryTeller
    lateinit var server: MinecraftServer

    private fun createStoryTeller() {

        //TODO CHANGE THIS LINE
        val method = StoryTellerType.REPEAT

        val time = 600
        when (method) {
            StoryTellerType.REPEAT -> storyTeller = RepeatStoryTeller(File("Repeat.txt"), time)
            StoryTellerType.EDA -> storyTeller = EDAStoryTeller(time)
        }
    }

    lateinit var logger: Logger
        private set
    lateinit var serial: Serial
        private set

    val MOUSE_DATA: MouseData = MouseData()
    val eventData: EventData = EventData()

    @EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        logger = event.modLog
    }

    @EventHandler
    fun init(event: FMLInitializationEvent) {
        serial = Serial(CommPortIdentifier.getPortIdentifier(
                //TODO this line gets changed too
                "COM4"))
                //"/dev/ttyS80"))
        Runtime.getRuntime().addShutdownHook(Thread(Runnable { serial.close() }))
        makeWorldCopy()
    }

    @EventHandler
    fun serverLoad(event: FMLServerStartingEvent) {
        event.registerServerCommand(StartCommand())
        event.registerServerCommand(StopCommand())
        event.registerServerCommand(CreeperCommand())
        //Minecraft.getMinecraft().soundHandler
        TestCommands.eventCommands.forEach {
            event.registerServerCommand(it)
        }
        server = event.server
    }

    private fun makeWorldCopy() {
        val masterWorld = File("../run/saves/backups/Master/")
        val destinationWorld = File("../run/saves/UseMe/")
        if (destinationWorld.exists()) {
            if (destinationWorld.isDirectory) {
                FileUtils.deleteDirectory(destinationWorld)
            }
            else {
                throw IllegalArgumentException("$destinationWorld is not a directory")
            }
        }
        if (masterWorld.exists()) {
            FileUtils.copyDirectory(masterWorld, destinationWorld)
        }
        else {
            throw IllegalArgumentException("$masterWorld does not exist")
        }
    }

    @EventHandler
    fun serverUnload(event: FMLServerStoppingEvent) {
        val player: EntityPlayer = Minecraft.getMinecraft().player
        GameSetup.stop(player)
    }

    var running = false
    var started = false

    @SubscribeEvent
    @JvmStatic
    fun onPlayerTick(event: TickEvent.PlayerTickEvent) {
        val player = event.player
        MOUSE_DATA.addData(player.pitchYaw.x, player.pitchYaw.y)

        if (!started) {
            started = true
            GameSetup.start(player)
        }

        if (running) {
            storyTeller.tick(player)

            //Blind the player every tick with a blindness potion lasting 17 ticks (it fades out in the last 20 ticks so we get a semi-blind effect)
            val potion = Potion.getPotionById(15)!!
            val potionEffect = PotionEffect(potion, 17, -2, true, false)
            player.addPotionEffect(potionEffect)

            //Make it always daytime
            player.world.worldTime = 20 * 1000

            //Make the player invulnerable
            player.health = 20f
        }
    }

    fun runAfter(millis: Long, func: () -> Unit) {
        timer.schedule(object : TimerTask() {
            override fun run() {
                func()
            }
        }, millis)
    }

    fun start() {
        createStoryTeller()
        running = true
    }

    fun stop(player: EntityPlayer) {
        storyTeller.stop(player)
        running = false
    }
}