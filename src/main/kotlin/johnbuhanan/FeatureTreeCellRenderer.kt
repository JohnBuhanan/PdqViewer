package com.johnbuhanan;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

class FeatureTreeCellRenderer extends JPanel implements TreeCellRenderer {
    JCheckBox checkBox = new JCheckBox();
    JLabel label = new JLabel();
    JToggleButton toggle = new JToggleButton("Real");

    public FeatureTreeCellRenderer() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        toggle.addActionListener(e -> toggle.setText(toggle.isSelected() ? "Fake" : "Real"));
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        removeAll();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        NodeData data = (NodeData) node.getUserObject();

        if (data.type == NodeType.FEATURE) {
            checkBox.setText(data.name);
            checkBox.setSelected(data.selected);
            add(checkBox);
        } else if (data.type == NodeType.LIBRARY) {
            label.setText(data.name);
            toggle.setEnabled(data.hasFake);
            toggle.setSelected(data.useFake);
            toggle.setText(data.useFake ? "Fake" : "Real");
            add(label);
            add(toggle);
        }

        return this;
    }
}