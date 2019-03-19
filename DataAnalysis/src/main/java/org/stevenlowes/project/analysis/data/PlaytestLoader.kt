package org.stevenlowes.project.analysis.data

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import java.io.File
import java.io.FileReader

object PlaytestLoader {
    private fun load(registry: ParticipantRegistry, file: File) =
        Playtest(registry[file.nameWithoutExtension.toInt()]!!, readJson(file))

    private fun readJson(file: File): JsonElement = JsonParser().parse(FileReader(file))

    private fun loadMany(registry: ParticipantRegistry, files: Array<File>) = files.map { load(registry, it) }

    private fun loadFolder(registry: ParticipantRegistry, folder: File) = loadMany(registry, folder.listFiles())

    fun loadAll(spreadsheet: File, dataFolder: File) = loadFolder(ParticipantRegistry(spreadsheet), dataFolder)
}