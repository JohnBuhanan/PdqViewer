package com.johnbuhanan.ui.model

import com.johnbuhanan.ui.model.Project.*
import javax.swing.tree.DefaultMutableTreeNode

val allProjects = mutableMapOf<String, Project>()

fun String.toFakeLibrary(): String = replace(":public", ":fake")

// :library:foo:public
private fun isLibrary(projectPath: String): Boolean {
    return projectPath.startsWith(":library") && projectPath.endsWith(":public")
}

// :feature:foo:internal
// :feature:foo:public
private fun isFeature(projectPath: String): Boolean {
    return projectPath.startsWith(":feature") &&
            (projectPath.endsWith(":public") || projectPath.endsWith(":internal"))
}

fun Project.toTreeNode(): DefaultMutableTreeNode {
    val treeNode = DefaultMutableTreeNode(this)

    for (dp in dependentProjects) {
        treeNode.add(dp.toTreeNode())
    }

    return treeNode
}

fun Project.addProject(projectPath: String): Project {
    val newProject = allProjects.getOrPut(projectPath) {
        when {
            isLibrary(projectPath) -> LibraryProject(projectPath)
            isFeature(projectPath) -> FeatureProject(projectPath)
            else -> throw IllegalStateException("Module name $projectPath not found")
        }
    }.also { add(it) }

    if (newProject is LibraryProject) {
        allProjects.getOrPut(projectPath.toFakeLibrary()) {
            FakeLibraryProject(projectPath).also { it.isSelected = false }
        }.also { add(it) }
    }

    return newProject
}

fun Project.add(project: Project) {
    dependentProjects.add(project)
}
