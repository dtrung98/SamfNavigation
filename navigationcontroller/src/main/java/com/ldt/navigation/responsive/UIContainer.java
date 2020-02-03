package com.ldt.navigation.responsive;

public interface UIContainer {
View createLayout(int containerId, int contentId);
void bind();
void attach();
void detach();
default void saveState();
default void restoreState();
}