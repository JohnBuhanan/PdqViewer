package com.johnbuhanan.featureselector.ui.tree

import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.treeStructure.Tree
import com.johnbuhanan.pdq.model.ProjectGraph
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.SwingUtilities
import javax.swing.UIManager
import javax.swing.plaf.basic.BasicTreeUI
import javax.swing.tree.DefaultTreeModel

class FeatureSelectorTreePanel(
    projectGraph: ProjectGraph,
) : JPanel(BorderLayout()) {

    private val treeGraph = TreeGraph(projectGraph)
    private val featurePaths = treeGraph.featurePaths
    private val comboBox = ComboBox(featurePaths.toTypedArray())
    private val tree = Tree().apply {
        cellRenderer = FeatureTreeCellRendererEditor()
        cellEditor = FeatureTreeCellRendererEditor()
        isEditable = true
        setShowsRootHandles(true)
        putClientProperty("JTree.lineStyle", "Angled")
    }

    init {
        UIManager.put("Tree.paintLines", true)
        tree.ui = BasicTreeUI()

        add(comboBox, BorderLayout.NORTH)
        add(JScrollPane(tree), BorderLayout.CENTER)

        // Initial tree
        updateTreeModel(featurePaths.first())

        // Listener
        comboBox.addActionListener {
            val selectedId = comboBox.selectedItem as? String ?: return@addActionListener
            updateTreeModel(selectedId)
        }
    }

    private fun updateTreeModel(featurePath: String) {
        val rootNode = treeGraph.allGraphNodes[featurePath]!!
        tree.model = DefaultTreeModel(rootNode)
        tree.isRootVisible = true
        expandAllRows()
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
