package com.johnbuhanan


import java.awt.*
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
    private val switch = SwitchButtonA().apply {
        preferredSize = Dimension(40, 10)
    }

    private var currentData: NodeData? = null

    init {
        switch.addActionListener {
            currentData?.useFake = switch.isSelected
            fireEditingStopped()
        }
        checkBox.addActionListener {
            currentData?.selected = checkBox.isSelected
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

    override fun getCellEditorValue(): Any? = currentData

    private fun buildComponent(value: Any?): JPanel {
        panel.removeAll()
        val node = value as? DefaultMutableTreeNode ?: return panel
        val data = node.userObject as? NodeData ?: return panel
        currentData = data

        if (data.type == NodeType.FEATURE) {
            checkBox.text = data.name
            checkBox.isSelected = data.selected
            panel.add(checkBox)
        } else if (data.type == NodeType.LIBRARY) {
            val iconLabel = JLabel(getRedIcon("/toolWindow/fake.png"))
            val nameLabel = JLabel(data.name)

            val switchButtonA = SwitchButtonA().apply {
                preferredSize = Dimension(40, checkBox.preferredSize.height)
            }

            panel.add(iconLabel)
            panel.add(nameLabel)
            panel.add(switchButtonA)
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

