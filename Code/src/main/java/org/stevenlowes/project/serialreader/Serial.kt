package org.stevenlowes.project.serialreader

import gnu.io.CommPortIdentifier
import gnu.io.SerialPort
import gnu.io.SerialPortEvent
import gnu.io.SerialPortEventListener
import org.stevenlowes.project.gui.util.PortSelector
import java.io.BufferedReader
import java.io.Closeable
import java.io.InputStreamReader

class Serial private constructor(port: CommPortIdentifier) : SerialPortEventListener, Closeable {

    private val serialPort: SerialPort
    var paused = true

    private val input: BufferedReader
    private val readings = mutableListOf<Pair<Long, Double>>()

    private var started = false
    private val startTime = System.currentTimeMillis() + 1000

    companion object {
        private const val timeOut = 2000
        private const val baudRate = 9600

        fun <T> withValidSerial(function: () -> T): T? {
            if (!serialSet) {
                val port = PortSelector.getPort() ?: return null
                SERIAL = Serial(port)
                serialSet = true
            }
            return function()
        }
    }

    init {
        serialPort = port.open(this.javaClass.name, timeOut) as SerialPort
        serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE)
        input = BufferedReader(InputStreamReader(serialPort.inputStream))
        serialPort.addEventListener(this)
        serialPort.notifyOnDataAvailable(true)
    }

    override fun serialEvent(oEvent: SerialPortEvent) {
        if (oEvent.eventType == SerialPortEvent.DATA_AVAILABLE) {
            val number = input.readLine().toDouble()
            if (started) {
                if (!paused) {
                    readings.add(System.currentTimeMillis() to number)
                }
            }
            else if (System.currentTimeMillis() > startTime) {
                started = true
            }
        }
    }

    fun consume(): List<Pair<Long, Double>> {
        val list = readings.toList()
        readings.clear()
        return list
    }

    override fun close() {
        serialPort.removeEventListener()
        serialPort.close()
        clear()
    }

    fun clear() {
        readings.clear()
    }
}

internal var serialSet = false
lateinit var SERIAL: Serial