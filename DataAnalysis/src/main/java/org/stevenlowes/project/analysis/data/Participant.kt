package org.stevenlowes.project.analysis.data

data class Participant(
    val id: Int,
    val scaredness: Int,
    val pair: Int?,
    val enjoyment: Int,
    val timing: Int
)