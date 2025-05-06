package com.johnbuhanan.pdq.model

import java.nio.file.Path
import kotlin.io.path.readText

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

fun String.toProjectNode(): ProjectNode {
    return ProjectNode(toProjectPath())
}

fun ProjectNode.readProjectDependencies(
    basePath: Path,
    allProjects: Map<String, ProjectNode>,
): Set<ProjectNode> {
    val buildFile = absolutePath(basePath).resolve("build.gradle")

    return buildFile.readText().lineSequence()
        .map { it.trim() }
        .mapNotNull { line ->
            val inferred = inferredProjectDependencies[line]
            val matched = PROJECT_REFERENCE_REGEX.find(line)?.groupValues?.get(1)
            inferred ?: matched
        }
        .mapNotNull { depPath -> allProjects[depPath.toProjectNode().projectPath] }
        .toSet()
}

private fun ProjectNode.absolutePath(basePath: Path): Path {
    return projectPath.toAbsolutePath(basePath)
}

private val PROJECT_REFERENCE_REGEX = Regex("""\bprojects\.((\w+\.)*\w+)""")

private val inferredProjectDependencies = mapOf(
    "snapshot()" to ":library:accessibility:testing",
    "uiTestSupport()" to ":library:android-test-utils:public"
)
