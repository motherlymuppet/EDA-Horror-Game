package org.stevenlowes.project.serialreader

import gnu.io.CommPortIdentifier
import gnu.io.SerialPort
import gnu.io.SerialPortEvent
import gnu.io.SerialPortEventListener
import java.io.BufferedReader
import java.io.Closeable
import java.io.InputStreamReader
import java.io.OutputStream

class Serial(comPort: Int, private val consumer: (Int) -> Unit) : SerialPortEventListener, Closeable {
    private val portName = "COM$comPort"

    private val serialPort: SerialPort

    private var eventCount = 0

    private val input: BufferedReader
    private val output: OutputStream

    companion object {
        private const val timeOut = 2000
        private const val baudRate = 9600
    }

    init {
        val portId = CommPortIdentifier.getPortIdentifier(portName) ?: throw IllegalArgumentException("Port not found")
        serialPort = portId.open(this.javaClass.name, timeOut) as SerialPort
        serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE)
        input = BufferedReader(InputStreamReader(serialPort.inputStream))
        output = serialPort.outputStream
        serialPort.addEventListener(this)
        serialPort.notifyOnDataAvailable(true)
    }

    @Synchronized
    override fun serialEvent(oEvent: SerialPortEvent) {
        if (oEvent.eventType == SerialPortEvent.DATA_AVAILABLE) {
            eventCount++
            val number = input.readLine().toInt()
            if(eventCount > 100){
                consumer(number)
            }
        }
    }

    @Synchronized
    override fun close() {
        serialPort.removeEventListener()
        serialPort.close()
    }
}