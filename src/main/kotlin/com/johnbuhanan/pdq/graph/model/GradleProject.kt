package com.johnbuhanan.pdq.graph.model

import com.johnbuhanan.pdq.graph.ext.toAbsolutePath
import com.johnbuhanan.pdq.graph.ext.toGradleProject
import java.nio.file.Path
import kotlin.io.path.readText

/** A wrapper around a gradle project definition **/
data class GradleProject(
    val projectPath: String, // e.g. :app:common
    val referencedBy: MutableSet<GradleProject> = mutableSetOf(),
    val dependsOn: MutableSet<GradleProject> = mutableSetOf()
) {
    override fun equals(other: Any?): Boolean {
        return other is GradleProject && other.projectPath == this.projectPath
    }

    override fun hashCode(): Int {
        return projectPath.hashCode()
    }

    override fun toString(): String {
        return "GradleProject(projectPath='$projectPath')"
    }

    private fun absolutePath(root: Path): Path {
        return projectPath.toAbsolutePath(root)
    }

    fun projectDependencies(
        root: Path,
        allProjects: Map<String, GradleProject>,
    ): Set<GradleProject> {
        val buildFile = absolutePath(root).resolve("build.gradle")

        return buildFile.readText().lineSequence()
            .map { it.trim() }
            .mapNotNull { line ->
                val inferred = inferredProjectDependencies[line]
                val matched = PROJECT_REFERENCE_REGEX.find(line)?.groupValues?.get(1)
                inferred ?: matched
            }
            .mapNotNull { depPath -> allProjects[depPath.toGradleProject().projectPath] }
            .toSet()
    }

    companion object {
        private val PROJECT_REFERENCE_REGEX = Regex("""\bprojects\.((\w+\.)*\w+)""")

        private val inferredProjectDependencies = mapOf(
            "snapshot()" to ":library:accessibility:testing",
            "uiTestSupport()" to ":library:android-test-utils:public"
        )
    }
}
