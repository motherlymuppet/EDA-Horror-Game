package com.stevenlowes.edahorror.data

class Mouse constructor(seconds: Long?){
    private val ms = seconds?.times(1000)

    private val mutableData = mutableListOf<Pair<Long, Angle>>()
    val data: List<Pair<Long, Angle>> get() = mutableData.toList()

    fun addData(pitch: Float, yaw: Float){
        val now = System.currentTimeMillis()
        mutableData.add(now to Angle(pitch, yaw))

        if(ms != null) {
            val startTime = now - (ms)
            while (mutableData.first().first < startTime) {
                mutableData.removeAt(0)
            }
        }
    }

    private fun recentData(ms: Long): List<Pair<Long, Angle>>? {
        val data = data
        val startTime = (data.lastOrNull() ?: return null).first - ms
        return data.subList(data.indexOfFirst { (time, _) -> time >= startTime }, data.size - 1)
    }

    private fun clear() {
        mutableData.clear()
    }
}

data class Angle(val pitch: Float, val yaw: Float)