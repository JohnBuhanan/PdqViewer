package johnbuhanan

import com.intellij.ui.treeStructure.Tree
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.SwingUtilities
import javax.swing.tree.DefaultMutableTreeNode

class FeatureSelectorTreePanel : JPanel() {
    private val tree by lazy {
        layout = BorderLayout()
        Tree(createNodesAndGetRoot()).apply {
            cellRenderer = FeatureTreeCellRenderer()
            rowHeight = 24
        }.also {
            add(JScrollPane(it), BorderLayout.CENTER)
        }
    }

    init {
        // Expand all rows after rendering
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
