package com.johnbuhanan

import java.awt.Component
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.AbstractCellEditor
import javax.swing.JCheckBox
import javax.swing.JPanel
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeCellEditor
import javax.swing.tree.TreeCellRenderer

internal class FeatureTreeCellRendererEditor : AbstractCellEditor(), TreeCellRenderer, TreeCellEditor {
    private val panel = JPanel(FlowLayout(FlowLayout.LEFT, 5, 0))
    private val checkBox = JCheckBox()
    private val toggle = SwitchButtonA()

    private var currentData: NodeData? = null

    init {
//        toggle.addActionListener {
//            currentData?.useFake = toggle.isSelected
//            toggle.text = if (toggle.isSelected) "Fake" else "Real"
//            fireEditingStopped()
//        }
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
            toggle.update(data)
            panel.add(toggle)
        }

        return panel
    }

    @Suppress("UseJBColor")
    private fun SwitchButtonA.update(nodeData: NodeData) {
//        text = nodeData.name
//        isSelected = nodeData.useFake
//        isEnabled = nodeData.hasFake
////        background = if (isSelected) Color(0xFFF59D) else Color(0xAED581)
//        foreground = Color.BLACK
////        border = LineBorder(Color.GRAY)
//
////        isFocusPainted = false
////        isBorderPainted = false
////        isContentAreaFilled = false
////        text = ""
        preferredSize = Dimension(40, 20)
//
//        addChangeListener {
//            if (isSelected) {
//                background = Color(0x4CAF50) // green
//            } else {
//                background = Color(0xBDBDBD) // gray
//            }
//        }
    }
}
