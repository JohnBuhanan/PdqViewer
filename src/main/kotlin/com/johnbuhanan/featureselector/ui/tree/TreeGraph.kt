package com.johnbuhanan.featureselector.ui.tree

import com.johnbuhanan.featureselector.model.SelectorGraph
import com.johnbuhanan.featureselector.model.SelectorNode
import com.johnbuhanan.pdq.model.ProjectGraph
import javax.swing.tree.DefaultMutableTreeNode

class TreeGraph(projectGraph: ProjectGraph) {
    val selectorGraph = SelectorGraph(projectGraph)
    val allGraphNodes = mutableMapOf<String, DefaultMutableTreeNode>()

    val rootNode: DefaultMutableTreeNode = selectorGraph.rootNode.toTreeNode()

    private fun SelectorNode.toTreeNode(): DefaultMutableTreeNode {
        val treeNode = allGraphNodes.getOrPut(projectPath) {
            DefaultMutableTreeNode(this)
        }

        for (dp in dependsOn) {
            treeNode.add(dp.toTreeNode())
        }

        return treeNode
    }

    val featurePaths: List<String> by lazy {
        selectorGraph.features
            .map { it.projectPath }
            .sorted()
    }
}
