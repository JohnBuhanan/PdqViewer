package com.johnbuhanan

internal class NodeData(var name: String, var type: NodeType) {
    var selected: Boolean = true
    var useFake: Boolean = false // only applies to libraries

    override fun toString(): String {
        return name
    }
}
