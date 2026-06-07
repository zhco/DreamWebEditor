package com.dreamweb.editor.domain.model

data class EditorSettings(
    val isDarkTheme: Boolean = false,
    val fontSize: Int = 14,
    val isAutoSaveEnabled: Boolean = true,
    val isLivePreviewEnabled: Boolean = true,
    val tabSize: Int = 4,
    val useSoftTabs: Boolean = true,
    val wordWrap: Boolean = true
)
