package com.johnbuhanan

import com.intellij.ui.treeStructure.Tree
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*
import javax.swing.plaf.basic.BasicTreeUI
import javax.swing.tree.DefaultMutableTreeNode

class FeatureSelectorTreePanel : JPanel() {
    private val tree by lazy {
        layout = BorderLayout()
        val buttonHeight = JToggleButton().preferredSize.height
        preferredSize = Dimension(200, buttonHeight) // adjust width as needed
        Tree(createNodesAndGetRoot()).apply {
            cellRenderer = FeatureTreeCellRendererEditor()
            cellEditor = FeatureTreeCellRendererEditor()
            isEditable = true
            setShowsRootHandles(true)
            isRootVisible = true
            putClientProperty("JTree.lineStyle", "Angled")
            rowHeight = buttonHeight
        }.also {
            add(JScrollPane(it), BorderLayout.CENTER)
        }
    }

    init {
        UIManager.put("Tree.paintLines", true)
        tree.ui = BasicTreeUI()
//        expandAllRows()
        tree.expandRow(0)
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


// :feature:home:internal -> :feature:detail:public
// :feature:home:internal -> :library:home:public
// :feature:detail:internal -> :feature:nowplaying:public
// :feature:detail:internal -> :library:detail:public
// :feature:nowplaying:internal -> n/a
// :feature:premium:internal -> :library:premium:public
// :feature:search:internal -> :library:search:public

private fun createNodesAndGetRoot(): DefaultMutableTreeNode {
    return DefaultMutableTreeNode(NodeData("app", NodeType.FEATURE)).apply {
        addHomeTree()
        addDetailTree()
        feature("nowplaying")
        feature("premium").apply {
            library("premium")
        }
        addSearchTree()
    }
}

private fun DefaultMutableTreeNode.addHomeTree() {
    feature("home").apply {
        addDetailTree()
        library("home")
    }
}

private fun DefaultMutableTreeNode.addDetailTree() {
    feature("detail").apply {
        feature("nowplaying")
        library("detail")
    }
}

private fun DefaultMutableTreeNode.addSearchTree() {
    feature("search").apply {
        library("search")
    }
}

private fun DefaultMutableTreeNode.feature(moduleName: String): DefaultMutableTreeNode {
    return add(moduleName, NodeType.FEATURE)
}

private fun DefaultMutableTreeNode.library(moduleName: String): DefaultMutableTreeNode {
    return add(moduleName, NodeType.LIBRARY)
}

private fun DefaultMutableTreeNode.add(moduleName: String, nodeType: NodeType): DefaultMutableTreeNode =
    DefaultMutableTreeNode(NodeData(moduleName, nodeType)).also { add(it) }
