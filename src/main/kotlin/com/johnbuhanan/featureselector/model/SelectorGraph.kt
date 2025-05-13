package com.johnbuhanan.featureselector.model

import com.johnbuhanan.pdq.model.ProjectGraph
import com.johnbuhanan.pdq.model.ProjectNode

class SelectorGraph(
    private val projectGraph: ProjectGraph,
) {

    private val allSelectorNodes: MutableMap<String, SelectorNode> = mutableMapOf()

    val rootNode: SelectorNode = projectGraph.rootNode.toSelectorNode()!!

    val features: List<SelectorNode> by lazy { projectGraph.features.map { it.toSelectorNode()!! } }

    private fun ProjectNode.toSelectorNode(): SelectorNode? {
        val selectorNode = projectPath.toSelectorNode()

        for (dp: ProjectNode in dependsOn) {
            val dps = dp.toSelectorNode()
            if (dps == selectorNode) {
                continue
            }
            dps?.let { selectorNode?.dependsOn?.add(it) }
        }

        return selectorNode
    }

    private fun String.toSelectorNode(): SelectorNode? {
        // if it contains an :internal entry, then store it as internal, otherwise public
        var effectivePath = replace(":public", ":internal")
        if (!projectGraph.allProjects.contains(effectivePath)) {
            effectivePath = this // No :internal? Back to default.
        }
        val selectorNode: SelectorNode = allSelectorNodes.getOrPut(effectivePath) {
            when {
                isRoot() -> SelectorNode.RootNode(effectivePath)
                isApp() -> SelectorNode.AppNode(effectivePath)
                isFeature() -> SelectorNode.FeatureNode(effectivePath)
                isLibrary() -> SelectorNode.LibraryNode(effectivePath)
                isFakeLibrary() -> SelectorNode.FakeLibraryNode(effectivePath)
                isSharedTest() -> SelectorNode.SharedTestProject(effectivePath)
                else -> return null
            }
        }

        return selectorNode
    }
}
