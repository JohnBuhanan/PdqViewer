package com.johnbuhanan


import com.intellij.icons.AllIcons
import com.johnbuhanan.model.Project
import com.johnbuhanan.model.toFakeLibrary
import java.awt.Color
import java.awt.Component
import java.awt.FlowLayout
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeCellEditor
import javax.swing.tree.TreeCellRenderer


internal class FeatureTreeCellRendererEditor : AbstractCellEditor(), TreeCellRenderer, TreeCellEditor {
    private val panel = JPanel(FlowLayout(FlowLayout.LEFT, 5, 0))
    private val checkBox = JCheckBox()
    private val radioButton = JRadioButton()
    private var currentProject: Project? = null

    init {
        checkBox.addActionListener {
            currentProject?.isSelected = checkBox.isSelected
            fireEditingStopped()
        }
        radioButton.addActionListener {
            currentProject?.isSelected = checkBox.isSelected
            fireEditingStopped()
        }
    }

    override fun getTreeCellRendererComponent(
        tree: JTree?, value: Any?, selected: Boolean, expanded: Boolean, leaf: Boolean, row: Int, hasFocus: Boolean
    ): Component {
        return buildComponent(value)
    }

    override fun getTreeCellEditorComponent(
        tree: JTree?, value: Any?, selected: Boolean, expanded: Boolean, leaf: Boolean, row: Int
    ): Component {
        return buildComponent(value)
    }

    override fun getCellEditorValue(): Any? = currentProject

    private fun buildComponent(value: Any?): JPanel {
        panel.removeAll()
        val treeNode = value as? DefaultMutableTreeNode ?: return panel
        val node = treeNode.userObject as? Project ?: return panel
        currentProject = node

        when (node) {
            is Project.LibraryProject -> {
                radioButton.isSelected = node.isSelected
                val iconLabel = JLabel(AllIcons.Nodes.Library)
                val nameLabel = JLabel(node.projectPath)
                panel.add(radioButton)
                panel.add(iconLabel)
                panel.add(nameLabel)
            }

            is Project.FakeLibraryProject -> {
                radioButton.isSelected = node.isSelected
                val iconLabel = JLabel(getRedIcon("/toolWindow/fake.png"))
                val nameLabel = JLabel(node.projectPath.toFakeLibrary())
                panel.add(radioButton)
                panel.add(iconLabel)
                panel.add(nameLabel)
            }

            is Project.AppProject, is Project.FeatureProject -> {
                checkBox.text = node.projectPath
                checkBox.isSelected = node.isSelected
                panel.add(checkBox)
            }
        }

        return panel
    }

    fun getRedIcon(resourcePath: String): Icon {
        // Load the original image as BufferedImage
        val originalImage = try {
            ImageIO.read(javaClass.getResource(resourcePath))
        } catch (e: IOException) {
            e.printStackTrace()
            return ImageIcon() // Return an empty icon in case of error
        }

        val width = originalImage.width
        val height = originalImage.height

        // Create a new BufferedImage to modify
        val redImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val g2d = redImage.createGraphics()
        g2d.drawImage(originalImage, 0, 0, null)
        g2d.dispose()

        // Iterate through the pixels and change black or dark pixels to red
        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = redImage.getRGB(x, y)

                // Get the alpha, red, green, and blue values
                val alpha = (pixel shr 24) and 0xFF
                val r = (pixel shr 16) and 0xFF
                val g = (pixel shr 8) and 0xFF
                val b = pixel and 0xFF

                // Change only non-transparent, dark pixels to red
                if (alpha > 0 && r < 50 && g < 50 && b < 50) {
                    redImage.setRGB(x, y, Color.RED.rgb)
                }
            }
        }

        val scaledImage = ImageIcon(redImage).image.getScaledInstance(15, 15, Image.SCALE_SMOOTH)
        return ImageIcon(scaledImage)
    }
}

