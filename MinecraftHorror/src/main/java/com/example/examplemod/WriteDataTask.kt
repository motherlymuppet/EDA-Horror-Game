package com.example.examplemod

import com.example.examplemod.serial.Serial
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class WriteDataTask: TimerTask(){
    companion object {
        val afterTimeMs = 2000L

        fun run(){
            MyMod.timer.schedule(WriteDataTask(), afterTimeMs)
        }
    }

    override fun run() {
        val sj = StringJoiner(System.lineSeparator())

        val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        sj.add(now)

        val currentMillis = System.currentTimeMillis()
        val getDataSince = currentMillis - afterTimeMs
        val data = MyMod.serial.data.filter { it.first >= getDataSince }
        data.forEach {
            sj.add("${it.first}:${it.second}")
        }

        val file = File("$now.txt")
        file.bufferedWriter().use {
            it.write(sj.toString())
        }
    }
}