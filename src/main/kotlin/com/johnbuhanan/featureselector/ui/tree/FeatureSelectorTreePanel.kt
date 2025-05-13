package com.johnbuhanan.featureselector.ui.tree

import com.intellij.ui.treeStructure.Tree
import com.johnbuhanan.featureselector.model.SelectorGraph
import com.johnbuhanan.featureselector.model.toTreeNode
import com.johnbuhanan.pdq.model.ProjectGraph
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.SwingUtilities
import javax.swing.UIManager
import javax.swing.plaf.basic.BasicTreeUI

class FeatureSelectorTreePanel(
    private val projectGraph: ProjectGraph,
) : JPanel() {
    // ComboBox up top?
    private val tree by lazy {
        layout = BorderLayout()

        val selectorGraph = SelectorGraph(projectGraph)

        val rootTreeNode = selectorGraph.rootNode.toTreeNode()
        Tree(rootTreeNode).apply {
            cellRenderer = FeatureTreeCellRendererEditor()
            cellEditor = FeatureTreeCellRendererEditor()
            isEditable = true
            setShowsRootHandles(true)
            isRootVisible = true
            putClientProperty("JTree.lineStyle", "Angled")
        }.also {
            add(JScrollPane(it), BorderLayout.CENTER)
        }
    }

    init {
        UIManager.put("Tree.paintLines", true)
        tree.ui = BasicTreeUI()
//        expandAllRows()
        tree.expandRow(0)
        tree.expandRow(1)
    }

    private fun expandAllRows() {
        SwingUtilities.invokeLater {
            var i = 0
            while (i < tree.rowCount) {
                tree.expandRow(i)
                i++
            }
        }
    }
}
