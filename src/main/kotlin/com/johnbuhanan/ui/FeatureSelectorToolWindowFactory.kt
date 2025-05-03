package com.johnbuhanan.ui

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.johnbuhanan.model.ProjectGraph
import com.johnbuhanan.ui.tree.FeatureSelectorTreePanel
import java.nio.file.Path
import kotlin.io.path.readLines

class FeatureSelectorToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val basePath = Path.of("/Users/johnbuhanan/StudioProjects/Songify")
//        val basePath = Path.of(project.basePath!!) // uncomment before publish
        val allProjectPaths: Set<String> = readAllProjectPaths(basePath)
        val projectGraph = ProjectGraph(basePath, allProjectPaths)
        val panel = FeatureSelectorTreePanel(projectGraph)

        @Suppress("removal")
        ContentFactory.SERVICE
            .getInstance()
            .createContent(panel, "", false)
            .also { toolWindow.contentManager.addContent(it) }
    }
}

private fun readAllProjectPaths(basePath: Path): Set<String> {
    val settingsFile = basePath.resolve("settings-all.gradle")
    val regex = Regex("\"(:[^\"]+)\"") // Match anything inside quotes
    val lines = settingsFile.readLines()

    return lines
        .flatMap { line ->
            regex.findAll(line).map { it.groupValues[1] }
        }
        .filterNot { it.contains(":app") || it.contains(":shared-test") } // Exclude ":app"
        .toSet()
}