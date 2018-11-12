package org.stevenlowes.project.tools

import org.stevenlowes.project.serialreader.Serial
import org.stevenlowes.project.spotifyAPI.Spotify
import org.stevenlowes.project.spotifyAPI.SpotifyAuth
import org.stevenlowes.tools.lifxcontroller.commands.request.light.RequestSetColor
import org.stevenlowes.tools.lifxcontroller.values.Color
import org.stevenlowes.tools.lifxcontroller.values.Hue
import org.stevenlowes.tools.lifxcontroller.values.Level
import java.lang.Double.max
import java.lang.Double.min
import java.net.InetAddress

/*
fun main(args: Array<String>){
    SpotifyAuth.refreshAuth()
    Spotify.play("22VdIZQfgXJea34mQxlt81")
}
*/

fun main(args: Array<String>){
    var last: Int? = null
    Serial {reading ->
        last = reading
    }

    var beginning: Int? = null
    val range = 1000.0

    val desk = InetAddress.getByName("192.168.8.138")
    val terrence = InetAddress.getByName("192.168.8.2")
    val wardrobe = InetAddress.getByName("192.168.8.3")
    val bedside = InetAddress.getByName("192.168.8.4")
    val shelf = InetAddress.getByName("192.168.8.5")
    val floor = InetAddress.getByName("192.168.8.6")
    val big = InetAddress.getByName("192.168.8.7")

    val allLights = setOf(
            desk,
            bedside,
            floor,
            shelf,
            wardrobe,
            terrence,
            big
                         )

    while(true){
        Thread.sleep(100)
        if(last != null){
            if(beginning == null){
                beginning = last
            }
            val new = (last!!-beginning!!+range*0.5)/range
            val bound = max(min(1.0, new), 0.0)
            println(new)
            RequestSetColor(color = Color(Hue.CYAN, Level.MAX, Level(bound)), duration = 100).send(allLights)
        }
    }
}