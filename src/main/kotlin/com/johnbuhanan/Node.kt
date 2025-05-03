package com.johnbuhanan

sealed class Node {
    abstract val name: String
    abstract var isSelected: Boolean
    val dependentNodes = mutableSetOf<Node>()

    data class RootNode(
        override val name: String,
        override var isSelected: Boolean = true,
    ) : Node()

    data class FeatureNode(
        override val name: String,
        override var isSelected: Boolean = true,
    ) : Node()

    data class LibraryNode(
        override val name: String,
        override var isSelected: Boolean = true,
    ) : Node()

    data class FakeLibraryNode(
        override val name: String,
        override var isSelected: Boolean = false,
    ) : Node()
}