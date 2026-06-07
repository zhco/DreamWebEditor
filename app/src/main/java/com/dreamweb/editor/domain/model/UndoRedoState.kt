package com.dreamweb.editor.domain.model

data class UndoRedoState(
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val currentContent: String = ""
)
