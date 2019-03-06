package com.stevenlowes.edahorror.data

class EventData {
    private val mutableData = mutableListOf<Pair<Long, String>>()
    val data: List<Pair<Long, String>> get() = mutableData.toList()

    fun addData(event: String){
        val now = System.currentTimeMillis()
        mutableData.add(now to event)
    }

    fun recentData(ms: Long): List<Pair<Long, String>> {
        val data = data
        val startTime = data.last().first - ms
        return data.subList(data.indexOfFirst { (time, _) -> time >= startTime }, data.size - 1)
    }

    private fun clear() {
        mutableData.clear()
    }
}