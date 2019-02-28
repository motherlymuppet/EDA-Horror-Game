package com.stevenlowes.edahorror.setup

import net.minecraft.client.audio.*
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundCategory

class Music : ISound{
    private var sound: Sound? = null
    private val resource = ResourceLocation("edahorror:horror_music")
    private var soundEvent: SoundEventAccessor? = null

    override fun createAccessor(handler: SoundHandler): SoundEventAccessor? {
        this.soundEvent = handler.getAccessor(resource)

        if (this.soundEvent == null) {
            this.sound = SoundHandler.MISSING_SOUND
        }
        else {
            this.sound = this.soundEvent!!.cloneEntry()
        }

        return this.soundEvent
    }

    override fun getXPosF() = 0f
    override fun getYPosF(): Float = 0f
    override fun getZPosF(): Float = 0f

    override fun getVolume() = 10f * (sound?.volume?:1f)
    override fun getPitch() = (sound?.pitch?:1f)

    override fun getRepeatDelay() = 0
    override fun canRepeat() = true

    override fun getSoundLocation() = resource
    override fun getCategory() = SoundCategory.MASTER
    override fun getAttenuationType() = ISound.AttenuationType.NONE
    override fun getSound() = sound!!
}