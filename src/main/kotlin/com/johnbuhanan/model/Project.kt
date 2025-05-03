package com.johnbuhanan.model

sealed class Project {
    abstract val projectPath: String
    abstract var isSelected: Boolean
    val dependentProjects = mutableSetOf<Project>()

    data class AppProject(
        override val projectPath: String,
        override var isSelected: Boolean = true,
    ) : Project()

    data class FeatureProject(
        override val projectPath: String,
        override var isSelected: Boolean = true,
    ) : Project()

    data class LibraryProject(
        override val projectPath: String,
        override var isSelected: Boolean = true,
    ) : Project()

    data class FakeLibraryProject(
        override val projectPath: String,
        override var isSelected: Boolean = false,
    ) : Project()
}
