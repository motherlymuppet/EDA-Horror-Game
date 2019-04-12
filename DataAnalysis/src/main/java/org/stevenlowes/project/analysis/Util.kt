package org.stevenlowes.project.analysis

import javafx.scene.paint.Color
import org.stevenlowes.project.analysis.gui.DataLabel

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
    val desiredValue = yStart + (yDelta * fraction)

    return desiredValue
}

operator fun <K, V : Number> Map<K, V>.times(multiplicand: Double) =
    mapValues { it.value.toDouble() * multiplicand }

fun <T : Comparable<T>> min(vararg elements: T) = elements.min()
fun <T : Comparable<T>> max(vararg elements: T) = elements.max()

fun <K> Map<K, Double>.getValueInterpolate(key: K): Double? where K : kotlin.Number, K : kotlin.Comparable<K> {
    val p1 = filter { it.key <= key }.maxBy { it.key }?.toPair() ?: return null
    val p2 = filter { it.key >= key }.minBy { it.key }?.toPair() ?: return null
    val interpolate = interpolate(key, p1, p2)
    return interpolate
}

fun <K> Map<K, Double>.plusValueInterpolate(key: K): Map<K, Double> where K : kotlin.Number, K : kotlin.Comparable<K> {
    val p1 = filter { it.key <= key }.maxBy { it.key }?.toPair() ?: return this
    val p2 = filter { it.key > key }.minBy { it.key }?.toPair() ?: return this
    val interpolate = interpolate(key, p1, p2) ?: return this
    return plus(key to interpolate)
}

fun Double.square() = this * this

fun List<Double>.stdDev(): Double {
    val mean = average()
    val sd = fold(0.0) { accumulator, next -> accumulator + (next - mean).square() }
    if(sd == 0.0){
        return 0.0
    }
    return Math.sqrt(sd / size - 1)
}

fun <K: Comparable<K>, V> Map<K, V>.firstValueAfter(x: K): V{
    val xKey = keys.filter { it > x }.min()!!
    return getValue(xKey)
}

fun <T : Comparable<T>> List<T>.median(): T {
    return sorted()[size / 2]
}

fun <T> List<T>.first(startIndex: Int, predicate: (T) -> Boolean): T? {
    (startIndex until size).forEach {
        val value = get(it)
        if (predicate(value)) return value
    }
    return null
}

fun List<DataLabel>.mapX(func: (DataLabel) -> Long): List<DataLabel> = map { label ->
    DataLabel(
        label.text,
        func(label),
        label.y,
        label.textAnchor
    )
}

fun List<DataLabel>.mapY(func: (DataLabel) -> Double): List<DataLabel> = map { label ->
    DataLabel(
        label.text,
        label.x,
        func(label),
        label.textAnchor
    )
}

val Color.hex: String get() = String.format("#%02x%02x%02x", (red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt())

val <A,S> ((A) -> S)?.nullIfNull get(): (A) -> S? = { a -> this?.invoke(a) }
val <A,B,S> ((A, B) -> S)?.nullIfNull get(): (A, B) -> S? = { a, b -> this?.invoke(a, b) }
val <A,B,C,S> ((A, B, C) -> S)?.nullIfNull get(): (A, B, C) -> S? = { a, b , c -> this?.invoke(a, b, c) }