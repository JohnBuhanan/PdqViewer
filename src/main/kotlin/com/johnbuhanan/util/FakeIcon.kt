package com.johnbuhanan.util

import java.awt.Color
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.Icon
import javax.swing.ImageIcon

fun getFakeIcon(): Icon {
    val originalImage = try {
        ImageIO.read(object {}.javaClass.getResource("/toolWindow/fake.png"))
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