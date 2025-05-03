package com.johnbuhanan.ui.model

sealed class Project {
    abstract val projectPath: String
    var isSelected: Boolean = true
    val dependentProjects = mutableSetOf<Project>()
    val displayName: String
        get() {
            return when {
                projectPath == ":app" -> ":app"
                else -> projectPath.split(":")[2]
            }
        }

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
}
