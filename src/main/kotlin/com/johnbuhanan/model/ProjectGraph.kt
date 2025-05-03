package com.johnbuhanan.model

import java.nio.file.Path

class ProjectGraph(
    basePath: Path,
    allProjectPaths: Set<String>,
) : Map<String, Project> {

    private val allProjects: Map<String, Project> by lazy {
        val projects = allProjectPaths.associateWith { it.toProject() }

        for (project in projects.values) {
            val projectDependencies = project.readProjectDependencies(basePath, projects)
            projectDependencies.forEach { pd ->
                pd.referencedBy.add(project)
            }
            project.dependsOn.addAll(projectDependencies)
        }

        projects
    }

    override val entries: Set<Map.Entry<String, Project>>
        get() = allProjects.entries
    override val keys: Set<String>
        get() = allProjects.keys
    override val size: Int
        get() = allProjects.size
    override val values: Collection<Project>
        get() = allProjects.values

    override fun isEmpty(): Boolean = allProjects.isEmpty()

    override fun get(key: String): Project? = allProjects[key]

    override fun containsValue(value: Project): Boolean = allProjects.containsValue(value)

    override fun containsKey(key: String): Boolean = allProjects.containsKey(key)

    override fun toString(): String {
        return allProjects.values.joinToString { it.toString() }
    }

    private fun bfsProjectsBy(
        starting: Set<Project>,
        neighborSelector: (Project) -> Set<Project>
    ): Set<Project> {
        val visited = mutableSetOf<Project>()
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

    val allFeatureProjects: Set<Project> by lazy {
        println(allProjects)
        val thing = allProjects.values.filter {
//            println(it.projectPath)
            it.projectPath.contains(":feature")
        }.toSet()
        thing
    }
}