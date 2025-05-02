package johnbuhanan

import java.awt.Component
import java.awt.FlowLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.*
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeCellRenderer

internal class FeatureTreeCellRenderer : JPanel(), TreeCellRenderer {
    var checkBox: JCheckBox = JCheckBox()
    var label: JLabel = JLabel()
    var toggle: JToggleButton = JToggleButton("Real")

    init {
        setLayout(FlowLayout(FlowLayout.LEFT, 5, 0))
        toggle.addActionListener(ActionListener { e: ActionEvent? -> toggle.setText(if (toggle.isSelected) "Fake" else "Real") })
    }

    override fun getTreeCellRendererComponent(
        tree: JTree?, value: Any?,
        selected: Boolean, expanded: Boolean, leaf: Boolean, row: Int, hasFocus: Boolean
    ): Component {
        removeAll()
        val node = value as DefaultMutableTreeNode
        val data = node.getUserObject() as NodeData

        if (data.type == NodeType.FEATURE) {
            checkBox.setText(data.name)
            checkBox.setSelected(data.selected)
            add(checkBox)
        } else if (data.type == NodeType.LIBRARY) {
            label.setText(data.name)
            toggle.setEnabled(data.hasFake)
            toggle.setSelected(data.useFake)
            toggle.setText(if (data.useFake) "Fake" else "Real")
            add(label)
            add(toggle)
        }

        return this
    }
}