package com.johnbuhanan;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

enum NodeType {FEATURE, LIBRARY}

class NodeData {
    String name;
    NodeType type;
    boolean selected = true;
    boolean hasFake = true;  // only applies to libraries
    boolean useFake = false; // only applies to libraries

    public NodeData(String name, NodeType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return name;
    }
}

public class FeatureSelectorTreePanel extends JPanel {

    public FeatureSelectorTreePanel() {
        setLayout(new BorderLayout());

        // Sample tree node data
        DefaultMutableTreeNode root = buildTreeModel();

        JTree tree = new JTree(root);
        tree.setCellRenderer(new FeatureTreeCellRenderer());
        tree.setRowHeight(24);

        JScrollPane scrollPane = new JScrollPane(tree);
        add(scrollPane, BorderLayout.CENTER);
    }

    private DefaultMutableTreeNode buildTreeModel() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new NodeData("Home", NodeType.FEATURE));
        // Build your full tree here (as in previous examples)
        return root;
    }
}
