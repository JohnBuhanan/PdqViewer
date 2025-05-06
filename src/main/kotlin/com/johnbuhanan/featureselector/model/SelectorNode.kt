package com.johnbuhanan.featureselector.model


sealed class SelectorNode {
    abstract val projectPath: String
    var isSelected: Boolean = true
    val dependsOn: MutableSet<SelectorNode> = mutableSetOf()

    val displayName: String
        get() {
            return when {
                projectPath.isRoot() || projectPath.isApp() -> projectPath
                else -> projectPath.split(":")[2]
            }
        }

    data class RootNode(
        override val projectPath: String,
    ) : SelectorNode()

    data class AppNode(
        override val projectPath: String,
    ) : SelectorNode()

    data class FeatureNode(
        override val projectPath: String,
    ) : SelectorNode()

    data class LibraryNode(
        override val projectPath: String,
    ) : SelectorNode()

    data class FakeLibraryNode(
        override val projectPath: String,
    ) : SelectorNode()

    data class SharedTestProject(
        override val projectPath: String,
    ) : SelectorNode()
}
