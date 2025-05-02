/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.johnbuhanan

import com.intellij.ui.Gray
import com.intellij.ui.JBColor
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JComponent

class SwitchButtonA : JComponent() {
    private var isSelected: Boolean = true
    private var slidePosition = 0

    init {
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(evt: MouseEvent?) {
                toggle()
                if (isSelected) {
                    setBackground(JBColor.DARK_GRAY)
                } else {
                    setBackground(JBColor.WHITE)
                }
            }
        })
    }

    fun toggle() {
        isSelected = !isSelected
        Thread(Runnable { animateToggle() }).start()
    }

    private fun animateToggle() {
        val start = if (isSelected) 0 else getWidth() - getHeight()
        val end = if (isSelected) getWidth() - getHeight() else 0

        for (i in 0..20) {
            slidePosition = start + (end - start) * i / 20
            repaint()
            try {
                Thread.sleep(10)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    @Suppress("UseJBColor")
    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        val g2d = g as Graphics2D

        // Enable antialiasing for smoother graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // Draw the background of the toggle
        g2d.color = if (isSelected) Gray._128 else Color.LIGHT_GRAY
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight()) // rounded corners

        // Draw the sliding button
        if (this.isSelected) {
            g2d.color = Color.black
        } else {
            g2d.color = Color.WHITE
        }
        g2d.fillOval(slidePosition, 0, getHeight(), getHeight())

        // Draw the text ("ON" or "OFF")
        g2d.color = if (isSelected) Color.WHITE else Color.BLACK
        val font = Font("Arial", Font.BOLD, 12)
        g2d.font = font
        val text = if (isSelected) "noonlight" else "fake"

        val textWidth = g2d.fontMetrics.stringWidth(text)
        val x = (getWidth() - textWidth) / 2
        val y = (getHeight() + g2d.fontMetrics.height) / 2 - 2

        g2d.drawString(text, x, y)
    }
}
