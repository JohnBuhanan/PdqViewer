package com.johnbuhanan

import com.intellij.ui.treeStructure.Tree
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.SwingUtilities
import javax.swing.UIManager
import javax.swing.plaf.basic.BasicTreeUI
import javax.swing.tree.DefaultMutableTreeNode

class FeatureSelectorTreePanel : JPanel() {
    private val tree by lazy {
        layout = BorderLayout()
        Tree(createNodesAndGetRoot()).apply {
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


// :feature:home:internal -> :feature:detail:public
// :feature:home:internal -> :library:home:public
// :feature:detail:internal -> :feature:nowplaying:public
// :feature:detail:internal -> :library:detail:public
// :feature:nowplaying:internal -> n/a
// :feature:premium:internal -> :library:premium:public
// :feature:search:internal -> :library:search:public
private fun createNodesAndGetRoot(): DefaultMutableTreeNode {
    return DefaultMutableTreeNode(Node.RootNode(":app")).apply {
        addHomeTree()
        addDetailTree()
        node(":feature:nowplaying:internal")
        node(":feature:premium:internal").apply {
            node(":library:premium:public")
        }
        addSearchTree()
    }
}

private fun DefaultMutableTreeNode.addHomeTree() {
    node(":feature:home:internal").apply {
        addDetailTree()
        node(":library:home:public")
    }
}

private fun DefaultMutableTreeNode.addDetailTree() {
    node(":feature:detail:internal").apply {
        node(":feature:nowplaying:public")
        node(":library:detail:public")
    }
}

private fun DefaultMutableTreeNode.addSearchTree() {
    node(":feature:search:internal").apply {
        node(":library:search:public")
    }
}

// :library:foo:public
private fun isLibrary(moduleName: String): Boolean {
    return moduleName.startsWith(":library") && moduleName.endsWith(":public")
}

// :feature:foo:internal
// :feature:foo:public
private fun isFeature(moduleName: String): Boolean {
    return moduleName.startsWith(":feature") &&
            (moduleName.endsWith(":public") || moduleName.endsWith(":internal"))
}

private fun DefaultMutableTreeNode.node(moduleName: String): DefaultMutableTreeNode {
    val node = allNodes.getOrPut(moduleName) {
        when {
            isLibrary(moduleName) -> Node.LibraryNode(moduleName)
            isFeature(moduleName) -> Node.FeatureNode(moduleName)
            else -> throw IllegalStateException("Module name $moduleName not found")
        }
    }

    return add(node)
}

private val allNodes = mutableMapOf<String, Node>()
private fun DefaultMutableTreeNode.add(node: Node): DefaultMutableTreeNode {
    return DefaultMutableTreeNode(node).also {
        add(it)
        addFakeIfNeeded(node.name)
    }
}

// What do we do for...
// :library:foo:public -> :library:foo:fake // We can't be guaranteed that a fake exists unless we check.
private fun DefaultMutableTreeNode.addFakeIfNeeded(nodeName: String) {
    val regex = Regex(""":library:([^:]+):fake""")
    val match = regex.find(nodeName)
    if (match == null) {
        return
    }
    add(
        Node.FakeLibraryNode(
            name = match.groupValues[1],
            isSelected = false,
        )
    )
}
