package com.johnbuhanan.graphview

import com.johnbuhanan.pdq.model.ProjectGraph
import org.graphstream.algorithm.BetweennessCentrality
import org.graphstream.graph.Graph
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
import kotlin.streams.asSequence

object GraphViewBuilder {
    fun buildGraphView(projectGraph: ProjectGraph): JComponent {
        System.setProperty("org.graphstream.ui", "swing")

        val graph = projectGraph.toMultiGraph()
        applyBetweennessCentrality(graph)

        val viewer = SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD).apply {
            enableAutoLayout()
        }

        val view = viewer.addDefaultView(false) as ViewPanel
        val camera = (view as View).camera

        val controls = JPanel().apply {
            add(JButton("+").apply {
                addActionListener {
                    zoom(camera, 0.8)
                    updateLabelsVisibility(view, graph)
                }
            })
            add(JButton("–").apply {
                addActionListener {
                    zoom(camera, 1.25)
                    updateLabelsVisibility(view, graph)
                }
            })
            add(JButton("Reset").apply {
                addActionListener {
                    camera.resetView()
                    camera.viewPercent = 1.0
                    updateLabelsVisibility(view, graph)
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

    private fun zoom(camera: Camera, delta: Double) {
        camera.viewPercent *= delta
        val x = camera.viewCenter.x
        val y = camera.viewCenter.y
        camera.setViewCenter(x + 0.0001, y, 0.0) // nudge to trigger repaint
    }

    fun updateLabelsVisibility(view: View, graph: Graph) {
        val threshold = 0.3
        val visible = view.camera.viewPercent < threshold
        for (node in graph) {
            node.setAttribute("ui.style", if (visible) "text-mode: normal;" else "text-mode: hidden;")
        }
    }

    private fun applyBetweennessCentrality(graph: Graph) {
        BetweennessCentrality().apply {
            init(graph)
            compute()
        }

        val maxCentrality = graph.nodes()
            .asSequence()
            .mapNotNull { it.getAttribute("Cb")?.toString()?.toDoubleOrNull() }
            .maxOrNull() ?: return

        graph.nodes().forEach { node ->
            val norm = (node.getAttribute("Cb")?.toString()?.toDoubleOrNull() ?: 0.0) / maxCentrality
            val color = interpolateColor(norm)
            node.setAttribute(
                "ui.style", """
                fill-color: rgb(${color.r},${color.g},${color.b});
                text-mode: hidden;
            """.trimIndent()
            )
            node.setAttribute("ui.size", 10 + norm * 30)
            node.setAttribute("ui.label", node.id) // Required for visibility toggle
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

    data class RGB(val r: Int, val g: Int, val b: Int)

    fun interpolateColor(norm: Double): RGB {
        val clamped = norm.coerceIn(0.0, 1.0)
        val r = if (clamped < 0.5) 255 else (255 * (1.0 - clamped) * 2).toInt()
        val g = if (clamped > 0.5) 255 else (255 * clamped * 2).toInt()
        return RGB(r, g, 0)
    }

    private val styleSheet = """
        node {
            size-mode: dyn-size;
            text-mode: hidden;
        }

        edge {
            fill-color: #99999905;
        }
    """.trimIndent()
}
