package com.stevenlowes.edahorror

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class WriteDataTask(val eventName: String): TimerTask(){
    companion object {
        val afterTimeMs = 5000L

        fun run(eventName: String){
            ModController.timer.schedule(WriteDataTask(eventName),
                                         afterTimeMs)
        }
    }

    override fun run() {
        ModController.logger.info("Writing GSR Data")

        val sj = StringJoiner(System.lineSeparator())
        sj.add(eventName)

        sj.add(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))

        val currentMillis = System.currentTimeMillis()
        val getDataSince = currentMillis - afterTimeMs
        val data = ModController.serial.data.filter { it.first >= getDataSince }
        data.forEach {
            sj.add("${it.first}:${it.second}")
        }

        val file = File("$eventName-${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))}.txt")
        ModController.logger.info("Done Writing GSR Data")

        file.printWriter().use {
            it.write(sj.toString())
        }
    }
}