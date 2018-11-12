package org.stevenlowes.project.gui.chart

import javafx.util.StringConverter
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class ChartTimeConverter(private val zoneOffset: ZoneOffset): StringConverter<Number>() {
    val formatter = DateTimeFormatter.ISO_DATE_TIME

    override fun toString(millis: Number): String {
        return toTime(millis).toString()
    }

    fun toTime(millis: Number): LocalDateTime{
        val longMillis = millis.toLong()
        val seconds = longMillis / 1000
        return LocalDateTime.ofEpochSecond(seconds, ((longMillis % 1000) * 1000 * 1000).toInt(), zoneOffset)
    }

    fun fromTime(time: LocalDateTime) = (time.toEpochSecond(zoneOffset) * 1000) + (time.nano / (1000 * 1000))

    override fun fromString(dateTimeString: String?): Number {
        if (dateTimeString == null) {
            return -1
        }
        val dateTime = LocalDateTime.parse(dateTimeString, formatter)
        val seconds = dateTime.toEpochSecond(zoneOffset)
        val millis = seconds * 1000
        return millis
    }
}