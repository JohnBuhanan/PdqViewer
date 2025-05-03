package com.johnbuhanan.ui

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.johnbuhanan.pdq.graph.model.GradleProjectGraph
import com.johnbuhanan.ui.tree.FeatureSelectorTreePanel
import java.nio.file.Path

class FeatureSelectorToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val basePath = "/Users/johnbuhanan/StudioProjects/TinderAndroid"
//        val basePath = project.basePath!! // uncomment before publish

        val gradleProjectGraph = GradleProjectGraph(
            root = Path.of(basePath),
            fileWithIncludedProjects = "settings-all.gradle",
        )
        val panel = FeatureSelectorTreePanel(gradleProjectGraph)

        @Suppress("removal")
        ContentFactory.SERVICE
            .getInstance()
            .createContent(panel, "", false)
            .also { toolWindow.contentManager.addContent(it) }
    }
}