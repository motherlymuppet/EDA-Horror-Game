package org.stevenlowes.project.gui.chart

import javafx.util.StringConverter
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class ChartTimeConverter(private val zoneOffset: ZoneOffset): StringConverter<Number>() {
    val formatter = DateTimeFormatter.ISO_DATE_TIME

    override fun toString(millis: Number): String {
        val seconds = millis.toLong() / 1000
        val dateTime = LocalDateTime.ofEpochSecond(seconds, 0, zoneOffset)
        return dateTime.toString()
    }

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