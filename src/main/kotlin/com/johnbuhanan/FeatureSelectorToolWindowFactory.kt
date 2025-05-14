package com.johnbuhanan

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.johnbuhanan.graphview.GraphViewBuilder
import com.johnbuhanan.pdq.model.ProjectGraph
import java.nio.file.Path


class FeatureSelectorToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val basePath = Path.of(project.basePath!!)
        val projectGraph = ProjectGraph(basePath)

        val moduleGraph = GraphViewBuilder.buildGraphView(projectGraph)
//        val featureSelector = FeatureSelectorTreePanel(projectGraph)

        val content = ContentFactory.getInstance().createContent(moduleGraph, "", false)
        toolWindow.contentManager.addContent(content)
    }
}
