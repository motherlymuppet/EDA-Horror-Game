package org.stevenlowes.project.analysis

fun <T> T.wrapWithList() = listOf(this)

fun <K, V> interpolate(desiredKey: K, p1: Pair<K, V>, p2: Pair<K, V>): Double? where K : Number, K : Comparable<K>, V : Number, V: Comparable<V> {
    val dKey = desiredKey.toDouble()
    val start = listOf(p1, p2).minBy { it.first }!!
    val end = listOf(p1, p2).maxBy { it.first }!!

    val xStart = start.first.toDouble()
    val xEnd = end.first.toDouble()
    val xDelta = xEnd - xStart

    if(xDelta == 0.0){
        return p1.second.toDouble()
    }

    val fraction = (dKey - xStart) / xDelta

    val yStart = start.second.toDouble()
    val yEnd = end.second.toDouble()
    val yDelta = yEnd - yStart
    return yStart + (yDelta * fraction)
}

operator fun <K, V : Number> Map<K, V>.times(multiplicand: Double) =
    mapValues { it.value.toDouble() * multiplicand }

fun <T : Comparable<T>> min(vararg elements: T) = elements.min()
fun <T : Comparable<T>> max(vararg elements: T) = elements.max()

fun Map<Long, Double>.getValueInterpolate(key: Long): Double? {
    val p1 = filter { it.key <= key }.maxBy { it.key }?.toPair() ?: return null
    val p2 = filter { it.key > key }.minBy { it.key }?.toPair() ?: return null
    val interpolate = interpolate(key, p1, p2)
    return interpolate
}

fun Map<Long, Double>.plusValueInterpolate(key: Long): Map<Long, Double> {
    val p1 = filter { it.key <= key }.maxBy { it.key }?.toPair() ?: return this
    val p2 = filter { it.key > key }.minBy { it.key }?.toPair() ?: return this
    val interpolate = interpolate(key, p1, p2) ?: return this
    return plus(key to interpolate)
}

fun Double.square() = this * this