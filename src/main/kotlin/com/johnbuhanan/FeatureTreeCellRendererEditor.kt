package com.johnbuhanan


import com.intellij.icons.AllIcons.Nodes.Library
import java.awt.Component
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.*
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeCellEditor
import javax.swing.tree.TreeCellRenderer


internal class FeatureTreeCellRendererEditor : AbstractCellEditor(), TreeCellRenderer, TreeCellEditor {
    private val panel = JPanel(FlowLayout(FlowLayout.LEFT, 5, 0))
    private val checkBox = JCheckBox()
    private val switch = SwitchButtonA().apply {
        preferredSize = Dimension(40, 10)
    }

    private var currentData: NodeData? = null

    init {
        switch.addActionListener {
            currentData?.useFake = switch.isSelected
            fireEditingStopped()
        }
        checkBox.addActionListener {
            currentData?.selected = checkBox.isSelected
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

    override fun getCellEditorValue(): Any? = currentData

    private fun buildComponent(value: Any?): JPanel {
        panel.removeAll()
        val node = value as? DefaultMutableTreeNode ?: return panel
        val data = node.userObject as? NodeData ?: return panel
        currentData = data

        if (data.type == NodeType.FEATURE) {
            checkBox.text = data.name
            checkBox.isSelected = data.selected
            panel.add(checkBox)
        } else if (data.type == NodeType.LIBRARY) {
            val iconLabel = JLabel(Library)
            val nameLabel = JLabel(data.name)
            val switchButtonA = SwitchButtonA()
            switchButtonA.preferredSize = Dimension(40, checkBox.preferredSize.height)

            panel.add(iconLabel)
            panel.add(nameLabel)
            panel.add(switchButtonA)
        }

        return panel
    }
}
