package com.johnbuhanan

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.johnbuhanan.pdq.model.ProjectGraph
import org.graphstream.graph.implementations.MultiGraph
import org.graphstream.ui.layout.springbox.implementations.SpringBox
import org.graphstream.ui.swing_viewer.DefaultView
import org.graphstream.ui.swing_viewer.SwingViewer
import org.graphstream.ui.view.GraphRenderer
import org.graphstream.ui.view.View
import org.graphstream.ui.view.Viewer
import java.awt.Graphics
import java.nio.file.Path
import javax.swing.JComponent


class FeatureSelectorToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {


        val basePath = Path.of(project.basePath!!)
        val projectGraph = ProjectGraph(basePath)
//        val subGraph = bfsProjectsBy(setOf(projectGraph.allProjects[":app:Twig"]!!)) { thing ->
//            thing.dependsOn
//        }
//        projectGraph.workingSet = subGraph
//        val selectorGraph = SelectorGraph(subGraph)
        val component = getGraphComponent(projectGraph)
//        val component = FeatureSelectorTreePanel(selectorGraph)

        @Suppress("removal")
        val content = ContentFactory.SERVICE.getInstance()
            .createContent(component, "", false)
        toolWindow.contentManager.addContent(content)
    }

    fun forceRedraw(view: JComponent) {
        val size = view.size
        view.setSize(size.width + 1, size.height + 1)
        view.setSize(size)
    }

    fun forceRenderRefresh(view: View) {
        // Slight nudge to the camera center
        val cam = view.camera
        val x = cam.viewCenter.x
        val y = cam.viewCenter.y
        cam.setViewCenter(x + 0.0001, y, 0.0)
    }

    private fun getGraphComponent(projectGraph: ProjectGraph): JComponent {
        System.setProperty("org.graphstream.ui", "swing")

        val layout = SpringBox(false).apply {
            stabilizationLimit = 0.95
        }

        val multiGraph = projectGraph.toMultiGraph()
        val viewer = SwingViewer(multiGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD).apply {
            enableAutoLayout(layout)
        }

        val view = viewer.addDefaultView(false) as JComponent
        val camera = (view as View).camera

        // Zoom helper
        fun zoom(delta: Double) {
            camera.viewPercent *= delta
//            forceRedraw(view)
            forceRenderRefresh(view)
//            view.repaint()
//            view.revalidate()
        }

        // UI panel with zoom controls
        val panel = javax.swing.JPanel(java.awt.BorderLayout())

        // Zoom buttons
        val controls = javax.swing.JPanel().apply {
            add(javax.swing.JButton("+").apply {
                addActionListener { zoom(0.8) } // Zoom in (lower % = zoom in)
            })
            add(javax.swing.JButton("–").apply {
                addActionListener { zoom(1.25) } // Zoom out (higher % = zoom out)
            })
            add(javax.swing.JButton("Reset").apply {
                addActionListener {
                    camera.resetView()
                    camera.viewPercent = 1.0
                }
            })
        }

        panel.add(view as JComponent, java.awt.BorderLayout.CENTER)
        panel.add(controls, java.awt.BorderLayout.SOUTH)

        return panel
    }
}

//class CustomView(viewer: Viewer, identifier: String, graphRenderer: GraphRenderer) :
//    DefaultView(viewer, identifier, graphRenderer) {
//    public override fun paintComponent(g: Graphics?) {
//        val stackElements = Thread.currentThread().getStackTrace()
//        for (i in stackElements.indices) {
//            if (stackElements[i].getClassName() == ToolWindows::class.java.getName()) {
//                repaint()
//                break
//            }
//        }
//        super.paintComponent(g)
//    }
//}

val styleSheet = """
node {
    size: 10px, 10px;
    fill-color: #3366cc;
}

edge {
	fill-color: #99999905;
}
""".trimIndent()

private fun ProjectGraph.toMultiGraph(): MultiGraph {
    val multiGraph = MultiGraph("MultiGraph").apply {
        isStrict = false
        setAttribute("ui.stylesheet", styleSheet)
//        setAttribute("ui.quality")
//        setAttribute("ui.antialias")
        setAttribute("layout.force", 0.001) // lower = less clumping
        setAttribute("layout.gravity", 0.001) // lower gravity helps
    }

    workingSet.forEach { projectNode ->
        val n = multiGraph.addNode(projectNode.projectPath)
//        n.setAttribute("ui.label", n.id)
        n.setAttribute("layout.weight", 100)
    }

    workingSet.forEach { projectNode ->
        projectNode.dependsOn.forEach { pd ->
            val a = projectNode.projectPath
            val b = pd.projectPath
            val edge = multiGraph.addEdge("$a$b", a, b, true)
            edge.setAttribute("layout.weight", 100)
        }
    }

    return multiGraph
}
