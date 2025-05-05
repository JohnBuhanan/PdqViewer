package com.johnbuhanan.pdq.model

data class ProjectNode(
    val projectPath: String
) {
    val referencedBy: MutableSet<ProjectNode> = mutableSetOf()
    val dependsOn: MutableSet<ProjectNode> = mutableSetOf()
}
