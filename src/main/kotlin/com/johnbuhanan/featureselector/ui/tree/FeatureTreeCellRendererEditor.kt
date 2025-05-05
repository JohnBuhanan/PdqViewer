package com.johnbuhanan.featureselector.ui.tree

import com.intellij.icons.AllIcons
import com.johnbuhanan.featureselector.model.SelectorNode
import com.johnbuhanan.featureselector.ui.util.getFakeIcon
import java.awt.Component
import java.awt.FlowLayout
import javax.swing.*
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeCellEditor
import javax.swing.tree.TreeCellRenderer

internal class FeatureTreeCellRendererEditor : AbstractCellEditor(), TreeCellRenderer, TreeCellEditor {
    private val panel = JPanel(FlowLayout(FlowLayout.LEFT, 5, 0))
    private val checkBox = JCheckBox()
    private val radioButton = JRadioButton()
    private var currentSelectorNode: SelectorNode? = null

    init {
        checkBox.addActionListener {
            currentSelectorNode?.isSelected = checkBox.isSelected
            fireEditingStopped()
        }
        radioButton.addActionListener {
            currentSelectorNode?.isSelected = checkBox.isSelected
            fireEditingStopped()
        }
    }

    override fun getTreeCellRendererComponent(
        tree: JTree?, value: Any?, selected: Boolean, expanded: Boolean, leaf: Boolean, row: Int, hasFocus: Boolean
    ): Component {
        return buildComponent(value)
    }

    override fun getTreeCellEditorComponent(
        tree: JTree?, value: Any?, selected: Boolean, expanded: Boolean, leaf: Boolean, row: Int
    ): Component {
        return buildComponent(value)
    }

    override fun getCellEditorValue(): Any? = currentSelectorNode

    private fun updateLibrary(selectorNode: SelectorNode, icon: Icon) {
        radioButton.isSelected = selectorNode.isSelected
        val iconLabel = JLabel(icon)
        val nameLabel = JLabel(selectorNode.displayName)
        panel.add(radioButton)
        panel.add(iconLabel)
        panel.add(nameLabel)
    }

    private fun buildComponent(value: Any?): JPanel {
        panel.removeAll()
        val treeNode = value as? DefaultMutableTreeNode ?: return panel
        val selectorNode = treeNode.userObject as? SelectorNode ?: return panel
        currentSelectorNode = selectorNode

        when (selectorNode) {
            is SelectorNode.LibraryNode -> {
                updateLibrary(selectorNode, AllIcons.Nodes.Library)
            }

            is SelectorNode.FakeLibraryNode -> {
                updateLibrary(selectorNode, getFakeIcon())
            }

            is SelectorNode.RootNode, is SelectorNode.FeatureNode, is SelectorNode.AppNode, is SelectorNode.SharedTestProject -> {
//                val iconLabel = JLabel(AllIcons.Nodes.Module)
                val nameLabel = JLabel(selectorNode.displayName)
                checkBox.isSelected = selectorNode.isSelected
                panel.add(checkBox)
//                panel.add(iconLabel)
                panel.add(nameLabel)
            }
        }

        return panel
    }
}
