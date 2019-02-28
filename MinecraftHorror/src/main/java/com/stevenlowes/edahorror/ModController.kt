package com.stevenlowes.edahorror

import com.stevenlowes.edahorror.ModController.MODID
import com.stevenlowes.edahorror.ModController.NAME
import com.stevenlowes.edahorror.ModController.VERSION
import com.stevenlowes.edahorror.data.Mouse
import com.stevenlowes.edahorror.events.TestCommands
import com.stevenlowes.edahorror.data.Serial
import com.stevenlowes.edahorror.setup.CreeperCommand
import com.stevenlowes.edahorror.setup.GameSetup
import com.stevenlowes.edahorror.setup.StartCommand
import com.stevenlowes.edahorror.setup.StopCommand
import com.stevenlowes.edahorror.storyteller.EDAStoryTeller
import com.stevenlowes.edahorror.storyteller.StoryTeller
import gnu.io.CommPortIdentifier
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.SoundRegistry
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
import org.apache.logging.log4j.Logger
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

    private fun createStoryTeller(){
        //storyTeller = RandomStoryTeller(10, 600)
        storyTeller = EDAStoryTeller(600)
    }

    lateinit var logger: Logger
        private set
    lateinit var serial: Serial
        private set

    val mouse: Mouse = Mouse(Consts.dataSeconds)

    @EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        logger = event.modLog
    }

    @EventHandler
    fun init(event: FMLInitializationEvent) {
        serial = Serial(CommPortIdentifier.getPortIdentifier(
                "COM4"), Consts.dataSeconds)
        Runtime.getRuntime().addShutdownHook(Thread(Runnable { serial.close() }))
    }

    @EventHandler
    fun serverLoad(event: FMLServerStartingEvent){
        event.registerServerCommand(StartCommand())
        event.registerServerCommand(StopCommand())
        event.registerServerCommand(CreeperCommand())
        Minecraft.getMinecraft().soundHandler
        TestCommands.eventCommands.forEach {
            event.registerServerCommand(it)
        }
        server = event.server
    }

    @EventHandler
    fun serverUnload(event: FMLServerStoppingEvent){
        val player: EntityPlayer = Minecraft.getMinecraft().player
        GameSetup.stop(player)
    }

    var running = false

    @SubscribeEvent
    @JvmStatic
    fun onPlayerTick(event: TickEvent.PlayerTickEvent) {
        val player = event.player
        mouse.addData(player.cameraPitch, player.cameraYaw)

        if(running){
            storyTeller.tick(player)

            val potion = Potion.getPotionById(15)!!
            val potionEffect = PotionEffect(potion, 15, -2, true, false)
            player.addPotionEffect(potionEffect)
            player.world.worldTime = 20 * 1000
        }
    }

    fun runAfter(millis: Long, func: () -> Unit){
        timer.schedule(object: TimerTask(){
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