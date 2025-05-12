package com.johnbuhanan.pdq.model

import java.nio.file.Path
import kotlin.io.path.readLines
import kotlin.io.path.readText

class ProjectGraph(
    basePath: Path,
) {
    val allProjects: Map<String, ProjectNode> by lazy { getAllProjects(basePath) }
    var workingSet: Set<ProjectNode> = allProjects.values.toSet()

    val rootNode: ProjectNode = ProjectNode("Songify").also {
        val androidApplication = allProjects[":Tinder"]!!
        it.dependsOn.add(androidApplication)
    }

    override fun toString(): String {
        return allProjects.values.joinToString { it.toString() }
    }
}

private fun getAllProjects(basePath: Path): Map<String, ProjectNode> {
    val projects = readAllProjectPaths(basePath).associateWith { it.toProjectNode() }

    for (project in projects.values) {
        val projectDependencies = project.readProjectDependencies(basePath, projects)
        projectDependencies.forEach { pd ->
            pd.referencedBy.add(project)
        }
        project.dependsOn.addAll(projectDependencies)
    }

    return projects
}

private fun readAllProjectPaths(basePath: Path): Set<String> {
    val settingsFile = basePath.resolve("settings-all.gradle")
    val regex = Regex("\'(:[^\"]+)\'") // Match anything inside quotes
    val lines = settingsFile.readLines()

    return lines
        .flatMap { line ->
            regex.findAll(line).map { it.groupValues[1] }
        }
//        .filterNot { it.contains(":app") || it.contains(":shared-test") } // Exclude ":app"
        .toSet()
}

private val projectReferenceRegex = Regex("""\bprojects\.((\w+\.)*\w+)""")

private val inferredProjectDependencies = mapOf(
    "snapshot()" to ":library:accessibility:testing",
    "uiTestSupport()" to ":library:android-test-utils:public"
)

private fun ProjectNode.readProjectDependencies(
    basePath: Path,
    allProjects: Map<String, ProjectNode>,
): Set<ProjectNode> {
    val buildFile = absolutePath(basePath).resolve("build.gradle")

    return buildFile.readText().lineSequence()
        .map { it.trim() }
        .mapNotNull { line ->
            val inferred = inferredProjectDependencies[line]
            val matched = projectReferenceRegex.find(line)?.groupValues?.get(1)
            inferred ?: matched
        }
        .mapNotNull { depPath -> allProjects[depPath.toProjectNode().projectPath] }
        .toSet()
}

private fun ProjectNode.absolutePath(basePath: Path): Path {
    return projectPath.toAbsolutePath(basePath)
}

fun bfsProjectsBy(
    starting: Set<ProjectNode>,
    neighborSelector: (ProjectNode) -> Set<ProjectNode>
): Set<ProjectNode> {
    val visited = mutableSetOf<ProjectNode>()
    val queue = ArrayDeque(starting)

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        if (current !in visited) {
            visited += current
            queue += neighborSelector(current).filterNot { it in visited }
        }
    }

    return visited
}