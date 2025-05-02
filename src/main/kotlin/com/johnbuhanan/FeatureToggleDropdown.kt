package com.johnbuhanan

import java.awt.BorderLayout
import javax.swing.JComboBox
import javax.swing.JPanel

class FeatureToggleDropdown(
    options: List<String>,
    selected: String,
    private val onChange: (String) -> Unit
) : JPanel() {

    private val comboBox = JComboBox(options.toTypedArray())

    init {
        layout = BorderLayout()
        add(comboBox, BorderLayout.CENTER)
        comboBox.selectedItem = selected

        comboBox.addActionListener {
            val newValue = comboBox.selectedItem as String
            onChange(newValue)
        }
    }

    fun getValue(): String = comboBox.selectedItem as String
}
