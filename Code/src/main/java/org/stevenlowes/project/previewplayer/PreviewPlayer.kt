package org.stevenlowes.project.previewplayer

import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels


class PreviewPlayer{
    companion object {
        private const val name = "preview.mp3"

        fun download(url: String){
            val website = URL(url)
            val rbc = Channels.newChannel(website.openStream())
            val fos = FileOutputStream(name)
            fos.channel.transferFrom(rbc, 0, java.lang.Long.MAX_VALUE)
        }

        fun play(){
            val media = Media(File(name).toURI().toString())
            val player = MediaPlayer(media)
            player.play()
        }
    }
}