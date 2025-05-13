// File: GraphViewBuilder.kt

package com.johnbuhanan.graphview

import com.johnbuhanan.pdq.model.ProjectGraph
import org.graphstream.algorithm.PageRank
import org.graphstream.graph.implementations.MultiGraph
import org.graphstream.ui.swing_viewer.SwingViewer
import org.graphstream.ui.swing_viewer.ViewPanel
import org.graphstream.ui.view.View
import org.graphstream.ui.view.Viewer
import org.graphstream.ui.view.camera.Camera
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

object GraphViewBuilder {
    fun buildComponent(projectGraph: ProjectGraph): JComponent {
        System.setProperty("org.graphstream.ui", "swing")

        val viewer = buildViewer(projectGraph)
        val view = viewer.addDefaultView(false) as ViewPanel
        val camera = (view as View).camera

        val controls = JPanel().apply {
            add(JButton("+").apply { addActionListener { zoom(camera, 0.8, view) } })
            add(JButton("–").apply { addActionListener { zoom(camera, 1.25, view) } })
            add(JButton("Reset").apply {
                addActionListener {
                    camera.resetView()
                    camera.viewPercent = 1.0
                }
            })
        }

        return JPanel().apply {
            layout = BorderLayout()
            add(view, BorderLayout.CENTER)
            add(controls, BorderLayout.SOUTH)
            preferredSize = Dimension(800, 600)
        }
    }

    private fun zoom(camera: Camera, delta: Double, view: View) {
        camera.viewPercent *= delta
        val x = camera.viewCenter.x
        val y = camera.viewCenter.y
        camera.setViewCenter(x + 0.0001, y, 0.0)
    }

    private fun buildViewer(projectGraph: ProjectGraph): Viewer {
        val graph = projectGraph.toMultiGraph()

        PageRank().apply {
            init(graph)
            compute()
        }

        return SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD).apply {
            enableAutoLayout()
        }
    }

    private fun ProjectGraph.toMultiGraph(): MultiGraph {
        val g = MultiGraph("MultiGraph").apply {
            isStrict = false
            setAttribute("ui.stylesheet", styleSheet)
            setAttribute("layout.force", 0.001)
            setAttribute("layout.gravity", 0.001)
        }

        workingSet.forEach {
            g.addNode(it.projectPath).apply {
                setAttribute("layout.weight", 100)
            }
        }

        workingSet.forEach { projectNode ->
            projectNode.dependsOn.forEach {
                g.addEdge("${projectNode.projectPath}${it.projectPath}", projectNode.projectPath, it.projectPath, true)
                    .setAttribute("layout.weight", 100)
            }
        }

        return g
    }

    private val styleSheet = """
        node {
            size: 10px, 10px;
            fill-mode: plain;
            fill-color: white;
            stroke-mode: plain;
            stroke-color: blue;
            stroke-width: 5px;
        }

        edge {
            fill-color: #99999905;
        }

        node.color0 { fill-color: red; }
        node.color1 { fill-color: blue; }
        node.color2 { fill-color: green; }
        node.color3 { fill-color: yellow; }
        node.color4 { fill-color: purple; }
    """.trimIndent()
}
