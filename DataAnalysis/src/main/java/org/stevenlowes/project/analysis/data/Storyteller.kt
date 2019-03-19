package org.stevenlowes.project.analysis.data

enum class Storyteller {
    EDA,
    REPEAT;

    companion object{
        fun from(name: String): Storyteller {
            return when {
                name.contains("EDAStoryTeller") -> EDA
                name.contains("RepeatStoryTeller") -> REPEAT
                else -> throw IllegalArgumentException("Unknown Storyteller $name")
            }
        }
    }
}