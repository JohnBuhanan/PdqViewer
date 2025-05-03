package com.johnbuhanan.pdq.graph.model

import com.johnbuhanan.pdq.graph.ext.toGradleProject
import java.nio.file.Path
import kotlin.io.path.readLines

class GradleProjectGraph(
    root: Path,
    private val fileWithIncludedProjects: String,
) : Map<String, GradleProject> {
    private val allProjectPaths: Set<String> = extractIncludedProjects(root)
    private val allProjects: Map<String, GradleProject> by lazy {
        val projects = allProjectPaths.associateWith { it.toGradleProject() }

        for (project in projects.values) {
            val dependencies = project.projectDependencies(root, projects)
            dependencies.forEach { dependency ->
                dependency.referencedBy.add(project)
            }
            project.dependsOn.addAll(dependencies)
        }

        projects
    }

    override val entries: Set<Map.Entry<String, GradleProject>>
        get() = allProjects.entries
    override val keys: Set<String>
        get() = allProjects.keys
    override val size: Int
        get() = allProjects.size
    override val values: Collection<GradleProject>
        get() = allProjects.values

    override fun isEmpty(): Boolean = allProjects.isEmpty()

    override fun get(key: String): GradleProject? = allProjects[key]

    override fun containsValue(value: GradleProject): Boolean = allProjects.containsValue(value)

    override fun containsKey(key: String): Boolean = allProjects.containsKey(key)

    override fun toString(): String {
        return allProjects.values.joinToString { it.toString() }
    }

    private fun extractIncludedProjects(root: Path): Set<String> {
        val settingsFile = root.resolve(fileWithIncludedProjects)
        val regex = Regex("""include\s+['"](:[^'"]+)['"]""")

        return settingsFile.readLines()
            .filter { it.contains("include") }
            .flatMap { regex.findAll(it).map { match -> match.groupValues[1] } }
            .toSet()
    }

    private fun bfsProjectsBy(
        starting: Set<GradleProject>,
        neighborSelector: (GradleProject) -> Set<GradleProject>
    ): Set<GradleProject> {
        val visited = mutableSetOf<GradleProject>()
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

    fun getAllProjectDependenciesRecursivelyFor(inputProjects: Set<GradleProject>): Set<GradleProject> {
        // To find downstream dependencies, we traverse the "dependsOn" relationships
        return bfsProjectsBy(inputProjects) { it.dependsOn }
    }
}

