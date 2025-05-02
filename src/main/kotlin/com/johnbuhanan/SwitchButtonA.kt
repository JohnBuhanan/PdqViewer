/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.johnbuhanan

import com.intellij.ui.Gray
import com.intellij.ui.JBColor
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JComponent

/**
 *
 * @author Java Programming with Aldrin
 */
class SwitchButtonA : JComponent() {
    private var isDarkMode: Boolean = false
    private var slidePosition = 0

    init {
        isDarkMode = false
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(evt: MouseEvent?) {
                toggle()
                if (this@SwitchButtonA.isDarkMode) {
                    setBackground(JBColor.DARK_GRAY)
                } else {
                    setBackground(JBColor.WHITE)
                }
            }
        })
    }

    fun toggle() {
        this.isDarkMode = !this.isDarkMode
        Thread(Runnable { animateToggle() }).start()
    }

    private fun animateToggle() {
        val start = if (this.isDarkMode) 0 else getWidth() - getHeight()
        val end = if (this.isDarkMode) getWidth() - getHeight() else 0

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
        g2d.color = if (this.isDarkMode) Gray._128 else Color.LIGHT_GRAY
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight()) // no gatter

        // Draw the sliding button
        if (this.isDarkMode) {
            g2d.color = Color.black
        } else {
            g2d.color = Color.WHITE
        }
        // Draw the sliding button
        g2d.fillOval(slidePosition, 0, getHeight(), getHeight())
    }
}