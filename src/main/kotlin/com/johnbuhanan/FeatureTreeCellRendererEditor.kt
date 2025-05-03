package com.johnbuhanan


import com.intellij.icons.AllIcons
import com.johnbuhanan.model.Project
import com.johnbuhanan.util.getFakeIcon
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
    private var currentProject: Project? = null

    init {
        checkBox.addActionListener {
            currentProject?.isSelected = checkBox.isSelected
            fireEditingStopped()
        }
        radioButton.addActionListener {
            currentProject?.isSelected = checkBox.isSelected
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

    override fun getCellEditorValue(): Any? = currentProject

    private fun updateLibrary(project: Project, icon: Icon) {
        radioButton.isSelected = project.isSelected
        val iconLabel = JLabel(icon)
        val nameLabel = JLabel(project.displayName)
        panel.add(radioButton)
        panel.add(iconLabel)
        panel.add(nameLabel)
    }

    private fun buildComponent(value: Any?): JPanel {
        panel.removeAll()
        val treeNode = value as? DefaultMutableTreeNode ?: return panel
        val project = treeNode.userObject as? Project ?: return panel
        currentProject = project

        when (project) {
            is Project.LibraryProject -> {
                updateLibrary(project, AllIcons.Nodes.Library)
            }

            is Project.FakeLibraryProject -> {
                updateLibrary(project, getFakeIcon())
            }

            is Project.AppProject, is Project.FeatureProject -> {
                checkBox.text = project.displayName
                checkBox.isSelected = project.isSelected
                panel.add(checkBox)
            }
        }

        return panel
    }
}
