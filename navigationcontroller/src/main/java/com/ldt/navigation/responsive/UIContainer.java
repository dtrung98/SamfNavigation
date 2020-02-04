package com.ldt.navigation.responsive;

public interface UIContainer {
void createLayout(int containerId, int contentId);
void bind();
void attach();
void detach();
default void saveState() {}
default void restoreState() {}
}