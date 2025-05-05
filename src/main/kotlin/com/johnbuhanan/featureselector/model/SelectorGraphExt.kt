package com.johnbuhanan.featureselector.model

import javax.swing.tree.DefaultMutableTreeNode

fun String.isLibrary(): Boolean {
    return startsWith(":library") &&
            (endsWith(":public") || endsWith(":internal"))
}

fun String.isFakeLibrary(): Boolean {
    return startsWith(":library") && endsWith(":fake")
}

fun String.isFeature(): Boolean {
    return startsWith(":feature") &&
            (endsWith(":public") || endsWith(":internal"))
}

fun String.isApp(): Boolean {
    return this == ":app"
}

fun String.isRoot(): Boolean {
    return this == "Songify"
}

fun String.isSharedTest(): Boolean {
    return endsWith(":shared-test")
}

fun SelectorNode.toTreeNode(): DefaultMutableTreeNode {
    val treeNode = DefaultMutableTreeNode(this)

    for (dp in dependsOn) {
        treeNode.add(dp.toTreeNode())
    }

    return treeNode
}