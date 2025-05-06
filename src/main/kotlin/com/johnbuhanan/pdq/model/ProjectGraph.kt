package com.johnbuhanan.pdq.model

import java.nio.file.Path
import kotlin.io.path.readLines

class ProjectGraph(
    basePath: Path,
) {
    val allProjects: Map<String, ProjectNode> by lazy {
        val projects = readAllProjectPaths(basePath).associateWith { it.toProject() }

        for (project in projects.values) {
            val projectDependencies = project.readProjectDependencies(basePath, projects)
            projectDependencies.forEach { pd ->
                pd.referencedBy.add(project)
            }
            project.dependsOn.addAll(projectDependencies)
        }

        projects
    }

    val rootNode: ProjectNode = ProjectNode("Songify").also {
        val androidApplication = allProjects[":app"]!!
        it.dependsOn.add(androidApplication)
    }

    override fun toString(): String {
        return allProjects.values.joinToString { it.toString() }
    }

    private fun readAllProjectPaths(basePath: Path): Set<String> {
        val settingsFile = basePath.resolve("settings-all.gradle")
        val regex = Regex("\"(:[^\"]+)\"") // Match anything inside quotes
        val lines = settingsFile.readLines()

        return lines
            .flatMap { line ->
                regex.findAll(line).map { it.groupValues[1] }
            }
//        .filterNot { it.contains(":app") || it.contains(":shared-test") } // Exclude ":app"
            .toSet()
    }

    private fun bfsProjectsBy(
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
}