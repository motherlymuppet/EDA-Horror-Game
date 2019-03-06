package com.stevenlowes.edahorror.data

class MouseData constructor(){
    private val mutableData = mutableListOf<Pair<Long, Angle>>()
    val data: List<Pair<Long, Angle>> get() = mutableData.toList()

    fun addData(pitch: Float, yaw: Float){
        val now = System.currentTimeMillis()
        mutableData.add(now to Angle(pitch, yaw))
    }

    fun recentData(ms: Long): List<Pair<Long, Angle>> {
        val data = data
        val startTime = data.last().first - ms
        return data.subList(data.indexOfFirst { (time, _) -> time >= startTime }, data.size - 1)
    }

    private fun clear() {
        mutableData.clear()
    }
}

data class Angle(val pitch: Float, val yaw: Float)