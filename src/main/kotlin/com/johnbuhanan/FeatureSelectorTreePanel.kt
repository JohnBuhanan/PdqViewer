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

    private fun createNodesAndGetRoot(): DefaultMutableTreeNode {
        return DefaultMutableTreeNode(NodeData("Home", NodeType.FEATURE)).apply {
            add(DefaultMutableTreeNode(NodeData("Dashboard", NodeType.FEATURE)).apply {
                add(DefaultMutableTreeNode(NodeData("AuthLib", NodeType.LIBRARY)))
                add(DefaultMutableTreeNode(NodeData("Profile", NodeType.FEATURE)).apply {
                    add(DefaultMutableTreeNode(NodeData("EditProfile", NodeType.FEATURE)).apply {
                        add(DefaultMutableTreeNode(NodeData("ImageLib", NodeType.LIBRARY)))
                    })
                    add(DefaultMutableTreeNode(NodeData("Settings", NodeType.FEATURE)).apply {
                        add(DefaultMutableTreeNode(NodeData("AnalyticsLib", NodeType.LIBRARY)).apply {
                            userObject = NodeData("AnalyticsLib", NodeType.LIBRARY)
                        })
                    })
                })
            })
            add(DefaultMutableTreeNode(NodeData("Music", NodeType.FEATURE)).apply {
                add(DefaultMutableTreeNode(NodeData("AudioLib", NodeType.LIBRARY)))
                add(DefaultMutableTreeNode(NodeData("Player", NodeType.FEATURE)).apply {
                    add(DefaultMutableTreeNode(NodeData("StorageLib", NodeType.LIBRARY)))
                })
            })
        }
    }
}
