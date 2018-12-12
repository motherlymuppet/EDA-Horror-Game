package com.example.examplemod.serial

import gnu.io.CommPortIdentifier
import gnu.io.SerialPort
import gnu.io.SerialPortEvent
import gnu.io.SerialPortEventListener
import java.io.BufferedReader
import java.io.Closeable
import java.io.InputStreamReader

class Serial constructor(port: CommPortIdentifier, seconds: Long?) : SerialPortEventListener, Closeable {

    private val serialPort: SerialPort
    private val ms = seconds?.times(1000)

    private val input: BufferedReader
    private val mutableData = mutableListOf<Pair<Long, Double>>()
    val data: List<Pair<Long, Double>> get() = mutableData.toList()

    companion object {
        private const val timeOut = 2000
        private const val baudRate = 9600
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

            if (number < 1000) {
                return
            }

            val now = System.currentTimeMillis()
            mutableData.add(now to number)

            if(ms != null) {
                val startTime = now - (ms)
                while (mutableData.first().first < startTime) {
                    mutableData.removeAt(0)
                }
            }
        }
    }

    fun gradient(ms: Long): Double?{
        val end = mutableData.lastOrNull() ?: return null

        val startTime = end.first - ms
        val start = mutableData.firstOrNull { (time, _) -> time >  startTime} ?: return null

        val valueDelta = end.second - start.second
        val timeDelta = end.first - start.first
        val gradient = valueDelta / timeDelta
        return gradient
    }

    override fun close() {
        serialPort.removeEventListener()
        serialPort.close()
        clear()
    }

    private fun clear() {
        mutableData.clear()
    }
}