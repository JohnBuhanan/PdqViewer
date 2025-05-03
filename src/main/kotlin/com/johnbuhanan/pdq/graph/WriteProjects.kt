package com.johnbuhanan.pdq.graph

import java.nio.file.Path
import kotlin.io.path.createFile
import kotlin.io.path.deleteIfExists

interface WriteProjects {
    fun write(to: Path, targets: List<String>, projects: Set<String>): Path
}

internal class WriteProjectsImpl : WriteProjects {
    override fun write(to: Path, targets: List<String>, projects: Set<String>): Path {
        // start fresh
        to.deleteIfExists()
        to.createFile()

        to.toFile().bufferedWriter().use { writer ->
            val header = """
        // THIS FILE IS GENERATED AUTOMATICALLY - DO NOT MODIFY
        // targets = ${targets.joinToString(" ")}
      """.trimIndent()
            writer.write(header)
            writer.newLine()

            projects.sorted().forEach {
                writer.write(it)
                writer.newLine()
            }
        }

        return to
    }
}
