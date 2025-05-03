package com.johnbuhanan.model

import com.johnbuhanan.model.Project.*
import java.nio.file.Path
import javax.swing.tree.DefaultMutableTreeNode
import kotlin.io.path.readText

val allProjects = mutableMapOf<String, Project>()

fun String.toFakeLibrary(): String = replace(":public", ":fake")

/**
 * Given an input string like
 * api/common-thing -> :api:common-thing
 * /api/common-thing -> :api:common-thing
 * :api:common-thing -> :api:common-thing
 * api.commonThing -> :api:common-thing
 */
fun String.toProjectPath(): String {
    return ":" + replace('/', ':')
        .replace('.', ':')
        .toKebab()
        .removePrefix(":")
        .removeSuffix(":")
}

/**
 * api/commonThing -> /User/someuser/repo/api/common-thing
 * /api/commonThing -> /User/someuser/repo/api/common-thing
 * :api:commonThing -> /User/someuser/repo/api/common-thing
 * :api:commonThing -> /User/someuser/repo/api/common-thing
 * .api.commonThing -> /User/someuser/repo/api/common-thing
 */
fun String.toAbsolutePath(basePath: Path): Path {
    val relativePath = removePrefix(":")
        .toKebab()
        .replace(':', '/')
        .replace('.', '/')

    return basePath.resolve(relativePath)
}

/**
 * api/commonThing -> api/common-thing
 * /api/commonThing -> /api/common-thing
 * api.commonThing -> api.common-thing
 */
private fun String.toKebab(): String {
    if (contains("TinderLite")) {
        return this
    }

    val kebab = replace(Regex("([a-z0-9])([A-Z])"), "$1-$2")
        .replace(Regex("([A-Z]+)([A-Z][a-z])"), "$1-$2")
        .lowercase()

    return kebab
}

fun Project.toTreeNode(): DefaultMutableTreeNode {
    val treeNode = DefaultMutableTreeNode(this)

    for (dp in dependsOn) {
        treeNode.add(dp.toTreeNode())
    }

    return treeNode
}

private fun isLibrary(projectPath: String): Boolean {
    return projectPath.startsWith(":library") &&
            (projectPath.endsWith(":public") || projectPath.endsWith(":internal"))
}

// :feature:foo:internal
// :feature:foo:public
private fun isFeature(projectPath: String): Boolean {
    return projectPath.startsWith(":feature") &&
            (projectPath.endsWith(":public") || projectPath.endsWith(":internal"))
}

private fun isApp(projectPath: String): Boolean {
    return projectPath.startsWith(":feature") && projectPath.endsWith(":app")
}

private fun isRoot(projectPath: String): Boolean {
    return projectPath == ":app"
}

private fun isSharedTest(projectPath: String): Boolean {
    return projectPath.endsWith(":shared-test")
}

fun Project.add(project: Project) {
    dependsOn.add(project)
}

fun String.toProject(): Project {
    val projectPath = toProjectPath()

    return when {
        isLibrary(projectPath) -> LibraryProject(projectPath)
        isFeature(projectPath) -> FeatureProject(projectPath)
        isApp(projectPath) -> AppProject(projectPath)
        isRoot(projectPath) -> RootProject(projectPath)
        isSharedTest(projectPath) -> SharedTestProject(projectPath)
        else -> throw IllegalStateException("Module name $projectPath not found")
    }
}

fun Project.readProjectDependencies(
    basePath: Path,
    allProjects: Map<String, Project>,
): Set<Project> {
    val buildFile = absolutePath(basePath).resolve("build.gradle")

    return buildFile.readText().lineSequence()
        .map { it.trim() }
        .mapNotNull { line ->
            val inferred = inferredProjectDependencies[line]
            val matched = PROJECT_REFERENCE_REGEX.find(line)?.groupValues?.get(1)
            inferred ?: matched
        }
        .mapNotNull { depPath -> allProjects[depPath.toProject().projectPath] }
        .toSet()
}

private fun Project.absolutePath(basePath: Path): Path {
    return projectPath.toAbsolutePath(basePath)
}

private val PROJECT_REFERENCE_REGEX = Regex("""\bprojects\.((\w+\.)*\w+)""")

private val inferredProjectDependencies = mapOf(
    "snapshot()" to ":library:accessibility:testing",
    "uiTestSupport()" to ":library:android-test-utils:public"
)
