package com.johnbuhanan.model

sealed class Project {
    abstract val projectPath: String
    var isSelected: Boolean = true
    val referencedBy: MutableSet<Project> = mutableSetOf()
    val dependsOn: MutableSet<Project> = mutableSetOf()

    val displayName: String
        get() {
            return when {
                projectPath == "Songify" -> "Songify"
                else -> {
                    projectPath.split(":")[2]
                }
            }
        }

    data class RootProject(
        override val projectPath: String,
    ) : Project()

    data class AppProject(
        override val projectPath: String,
    ) : Project()

    data class FeatureProject(
        override val projectPath: String,
    ) : Project()

    data class LibraryProject(
        override val projectPath: String,
    ) : Project()

    data class FakeLibraryProject(
        override val projectPath: String,
    ) : Project()

    data class SharedTestProject(
        override val projectPath: String,
    ) : Project()
}
