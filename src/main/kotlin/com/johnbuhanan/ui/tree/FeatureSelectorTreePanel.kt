package com.johnbuhanan.ui.tree

import com.intellij.ui.treeStructure.Tree
import com.johnbuhanan.pdq.graph.model.GradleProjectGraph
import com.johnbuhanan.ui.model.Project
import com.johnbuhanan.ui.model.addProject
import com.johnbuhanan.ui.model.toTreeNode
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.SwingUtilities
import javax.swing.UIManager
import javax.swing.plaf.basic.BasicTreeUI

class FeatureSelectorTreePanel(
    private val gradleProjectGraph: GradleProjectGraph,
) : JPanel() {
    private val tree by lazy {
        layout = BorderLayout()
        val rootProject = createProjects()
        val rootTreeNode = rootProject.toTreeNode()
        Tree(rootTreeNode).apply {
            cellRenderer = FeatureTreeCellRendererEditor()
            cellEditor = FeatureTreeCellRendererEditor()
            isEditable = true
            setShowsRootHandles(true)
            isRootVisible = true
            putClientProperty("JTree.lineStyle", "Angled")
        }.also {
            add(JScrollPane(it), BorderLayout.CENTER)
        }
    }

    init {
        UIManager.put("Tree.paintLines", true)
        tree.ui = BasicTreeUI()
//        expandAllRows()
        tree.expandRow(0)
        tree.expandRow(1)
    }

    private fun expandAllRows() {
        SwingUtilities.invokeLater {
            var i = 0
            while (i < tree.rowCount) {
                tree.expandRow(i)
                i++
            }
        }
    }
}

// :feature:home:internal -> :feature:detail:public
// :feature:home:internal -> :library:home:public
// :feature:detail:internal -> :feature:nowplaying:public
// :feature:detail:internal -> :library:detail:public
// :feature:nowplaying:internal -> n/a
// :feature:premium:internal -> :library:premium:public
// :feature:search:internal -> :library:search:public
private fun createProjects(): Project {
    return Project.AppProject(":app").apply {
        addHomeProject()
        addDetailProject()
        addProject(":feature:nowplaying:internal")
        addProject(":feature:premium:internal").apply {
            addProject(":library:premium:public")
        }
        addSearchProject()
    }
}

private fun Project.addHomeProject() {
    addProject(":feature:home:internal").apply {
        addDetailProject()
        addProject(":library:home:public")
    }
}

private fun Project.addDetailProject() {
    addProject(":feature:detail:internal").apply {
        addProject(":feature:nowplaying:public")
        addProject(":library:detail:public")
    }
}

private fun Project.addSearchProject() {
    addProject(":feature:search:internal").apply {
        addProject(":library:search:public")
    }
}
