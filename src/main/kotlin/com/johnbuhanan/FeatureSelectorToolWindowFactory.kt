package com.johnbuhanan

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.johnbuhanan.featureselector.model.SelectorGraph
import com.johnbuhanan.featureselector.ui.tree.FeatureSelectorTreePanel
import com.johnbuhanan.pdq.model.ProjectGraph
import org.graphstream.graph.implementations.MultiGraph
import java.nio.file.Path


class FeatureSelectorToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        System.setProperty("org.graphstream.ui", "swing")
        val basePath = Path.of(project.basePath!!)
        val projectGraph = ProjectGraph(basePath)
        val selectorGraph = SelectorGraph(projectGraph)
        val multiGraph = projectGraph.toMultiGraph()
        multiGraph.display()
        val panel = FeatureSelectorTreePanel(selectorGraph)

        @Suppress("removal")
        ContentFactory.SERVICE
            .getInstance()
            .createContent(panel, "", false)
            .also { toolWindow.contentManager.addContent(it) }
    }
}

private fun ProjectGraph.toMultiGraph(): MultiGraph {
    val multiGraph = MultiGraph("MultiGraph")
    multiGraph.isStrict = false

    allProjects.forEach { project ->
        val projectNode = project.value
        multiGraph.addNode(projectNode.projectPath)
    }

    allProjects.forEach { project ->
        val projectNode = project.value
        projectNode.dependsOn.forEach { pd ->
            val a = projectNode.projectPath
            val b = pd.projectPath
            multiGraph.addEdge("$a$b", a, b)
        }
    }

    return multiGraph
}
