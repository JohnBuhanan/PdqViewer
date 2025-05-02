package com.johnbuhanan;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import javax.swing.*;

public class SelectFeaturesAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        SwingUtilities.invokeLater(() -> {
            FeatureSelectorDialog.main(null); // or refactor to open dialog properly
        });
    }
}
