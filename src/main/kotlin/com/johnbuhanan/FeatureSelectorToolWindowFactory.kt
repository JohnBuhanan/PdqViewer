package com.johnbuhanan

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.johnbuhanan.featureselector.model.SelectorGraph
import com.johnbuhanan.featureselector.ui.tree.FeatureSelectorTreePanel
import com.johnbuhanan.pdq.model.ProjectGraph
import java.nio.file.Path

class FeatureSelectorToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val basePath = Path.of("/Users/johnbuhanan/StudioProjects/Songify")
//        val basePath = Path.of(project.basePath!!) // uncomment before publish
        val projectGraph = ProjectGraph(basePath)
        val selectorGraph = SelectorGraph(projectGraph)
        val panel = FeatureSelectorTreePanel(selectorGraph)

        @Suppress("removal")
        ContentFactory.SERVICE
            .getInstance()
            .createContent(panel, "", false)
            .also { toolWindow.contentManager.addContent(it) }
    }
}
