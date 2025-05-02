package com.johnbuhanan;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

public class FeatureSelectorDialog {
    enum NodeType {FEATURE, LIBRARY}

    static class NodeData {
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

    static class FeatureTreeCellRenderer extends JPanel implements TreeCellRenderer {
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Select Features");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            DefaultMutableTreeNode root = new DefaultMutableTreeNode(new NodeData("Home", NodeType.FEATURE));
            DefaultMutableTreeNode dashboard = new DefaultMutableTreeNode(new NodeData("Dashboard", NodeType.FEATURE));
            dashboard.add(new DefaultMutableTreeNode(new NodeData("AuthLib", NodeType.LIBRARY)));
            DefaultMutableTreeNode profile = new DefaultMutableTreeNode(new NodeData("Profile", NodeType.FEATURE));
            DefaultMutableTreeNode editProfile = new DefaultMutableTreeNode(new NodeData("EditProfile", NodeType.FEATURE));
            editProfile.add(new DefaultMutableTreeNode(new NodeData("ImageLib", NodeType.LIBRARY)));
            profile.add(editProfile);
            profile.add(new DefaultMutableTreeNode(new NodeData("Settings", NodeType.FEATURE)));
            dashboard.add(profile);
            root.add(dashboard);

            DefaultMutableTreeNode music = new DefaultMutableTreeNode(new NodeData("Music", NodeType.FEATURE));
            music.add(new DefaultMutableTreeNode(new NodeData("AudioLib", NodeType.LIBRARY)));
            DefaultMutableTreeNode player = new DefaultMutableTreeNode(new NodeData("Player", NodeType.FEATURE));
            player.add(new DefaultMutableTreeNode(new NodeData("StorageLib", NodeType.LIBRARY)));
            music.add(player);
            root.add(music);

            JTree tree = new JTree(root);
            tree.setCellRenderer(new FeatureTreeCellRenderer());
            tree.setRowHeight(24);

            JScrollPane scrollPane = new JScrollPane(tree);
            frame.add(scrollPane, BorderLayout.CENTER);

            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bottom.add(new JButton("Collapse All"));
            bottom.add(new JButton("Finish"));
            frame.add(bottom, BorderLayout.SOUTH);

            frame.setSize(500, 500);
            frame.setVisible(true);
        });
    }
}
