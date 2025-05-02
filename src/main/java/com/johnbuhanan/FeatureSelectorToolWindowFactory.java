package com.johnbuhanan;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

// FeatureSelectorToolWindowFactory.java
public class FeatureSelectorToolWindowFactory implements ToolWindowFactory, DumbAware {
    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        FeatureSelectorTreePanel panel = new FeatureSelectorTreePanel();
        @SuppressWarnings("removal")
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(panel, "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
