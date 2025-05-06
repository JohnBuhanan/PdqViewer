package com.johnbuhanan.pdq.model

import java.nio.file.Path

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
