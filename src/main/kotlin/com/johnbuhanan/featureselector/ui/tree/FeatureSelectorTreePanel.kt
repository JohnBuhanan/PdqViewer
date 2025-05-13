package com.johnbuhanan.featureselector.ui.tree

import com.intellij.openapi.ui.ComboBox
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
import javax.swing.tree.DefaultTreeModel

class FeatureSelectorTreePanel(
    projectGraph: ProjectGraph,
) : JPanel(BorderLayout()) {

    private val selectorGraph = SelectorGraph(projectGraph)
    private val featureList = selectorGraph.features.map { it.projectPath }.sorted()
    private val comboBox = ComboBox(featureList.toTypedArray())
    private val tree = Tree().apply {
        cellRenderer = FeatureTreeCellRendererEditor()
        cellEditor = FeatureTreeCellRendererEditor()
        isEditable = true
        setShowsRootHandles(true)
        isRootVisible = true
        putClientProperty("JTree.lineStyle", "Angled")
    }

    init {
        UIManager.put("Tree.paintLines", true)
        tree.ui = BasicTreeUI()

        // Top: dropdown
        add(comboBox, BorderLayout.NORTH)

        // Center: scrollable tree
        add(JScrollPane(tree), BorderLayout.CENTER)

        // Set initial tree
        updateTreeModel(featureList.first())

        comboBox.addActionListener {
            val selectedId = comboBox.selectedItem as? String ?: return@addActionListener
            updateTreeModel(selectedId)
        }
    }

    private fun updateTreeModel(featurePath: String) {
        val selected = selectorGraph.features.find { it.projectPath == featurePath } ?: return
        val rootNode = selected.toTreeNode()
        tree.model = DefaultTreeModel(rootNode)
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
