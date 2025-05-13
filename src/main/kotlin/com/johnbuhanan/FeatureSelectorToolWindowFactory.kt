package com.johnbuhanan

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.johnbuhanan.pdq.model.ProjectGraph
import org.graphstream.algorithm.PageRank
import org.graphstream.graph.implementations.MultiGraph
import org.graphstream.ui.swing_viewer.SwingViewer
import org.graphstream.ui.swing_viewer.ViewPanel
import org.graphstream.ui.view.View
import org.graphstream.ui.view.Viewer
import java.nio.file.Path
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

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

    private fun getViewerWithAlgAndLayout(projectGraph: ProjectGraph): Viewer {
        val multiGraph = projectGraph.toMultiGraph()

        /**
         * ======ALGORITHM======
         */
        //    val tscc = TarjanStronglyConnectedComponents()
        //    tscc.init(multiGraph)
        //    tscc.compute()

        //    val bc = BetweennessCentrality()
        //    bc.init(multiGraph)
        //    bc.compute()

        //    val tsk = TopologicalSortDFS()
        //    tsk.init(multiGraph)
        //    tsk.compute()
        //    multiGraph.nodes().forEach { n ->
        //        n.setAttribute("label", n.getAttribute(tscc.sccIndexAttribute))
        //    }

        //    val wp = WelshPowell()
        //    wp.init(multiGraph)
        //    wp.compute()
        //    for (node in multiGraph.nodes()) {
        //        val color: Int = node.getAttribute("WelshPowell.color").toString().toInt()
        //        node.setAttribute("ui.class", "color$color")
        //    }
        //        val layout = SpringBox(false).apply {
        //            stabilizationLimit = 0.9
        //        }
        val pr = PageRank()
        pr.init(multiGraph)
        pr.compute()

        /**
         * ======LAYOUT======
         */
//        val layout = LinLog()

        // val layout = HierarchicalLayout().apply {
        //     stabilizationLimit = 0.35
        // }

//        val layout = SpringBox()
        return SwingViewer(multiGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD).apply {
//            enableAutoLayout(layout)
            enableAutoLayout()
        }
    }

    private fun getGraphComponent(projectGraph: ProjectGraph): JComponent {
        System.setProperty("org.graphstream.ui", "swing")

        val viewer = getViewerWithAlgAndLayout(projectGraph)

        val view = viewer.addDefaultView(false) as ViewPanel
        val camera = (view as View).camera

        // Zoom helper
        fun zoom(delta: Double) {
            camera.viewPercent *= delta
//            forceRedraw(view)
            forceRenderRefresh(view)
//            view.repaint()
//            view.revalidate()
        }

        // Zoom buttons
        val controls = JPanel().apply {
            add(JButton("+").apply {
                addActionListener { zoom(0.8) } // Zoom in (lower % = zoom in)
            })
            add(JButton("–").apply {
                addActionListener { zoom(1.25) } // Zoom out (higher % = zoom out)
            })
            add(JButton("Reset").apply {
                addActionListener {
                    camera.resetView()
                    camera.viewPercent = 1.0
                }
            })
        }

        return view
    }
}

val styleSheet = """
node {
    size: 10px, 10px;
//    shape: box;
    fill-mode: plain;   /* Default.          */
    fill-color: white;    /* Default is black. */
    stroke-mode: plain; /* Default is none.  */
    stroke-color: blue; /* Default is black. */
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
