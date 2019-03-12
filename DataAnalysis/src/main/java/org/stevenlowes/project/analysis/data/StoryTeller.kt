package org.stevenlowes.project.analysis.data

enum class StoryTeller {
    EDA,
    REPEAT;

    companion object{
        fun from(name: String): StoryTeller {
            return when {
                name.contains("EDAStoryTeller") -> EDA
                name.contains("RepeatStoryTeller") -> REPEAT
                else -> throw IllegalArgumentException("Unknown Storyteller $name")
            }
        }
    }
}