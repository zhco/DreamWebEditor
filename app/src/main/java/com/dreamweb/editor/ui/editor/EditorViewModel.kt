package com.dreamweb.editor.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreamweb.editor.domain.model.ProjectFile
import com.dreamweb.editor.domain.model.UndoRedoState
import com.dreamweb.editor.domain.usecase.FormatHtmlUseCase
import com.dreamweb.editor.domain.usecase.SaveFileUseCase
import com.dreamweb.editor.data.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditorUiState(
    val currentFile: ProjectFile? = null,
    val editedContent: String = "",
    val isModified: Boolean = false,
    val undoRedo: UndoRedoState = UndoRedoState(),
    val isSaving: Boolean = false,
    val saveMessage: String? = null,
    val error: String? = null
)

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val saveFileUseCase: SaveFileUseCase,
    private val formatHtmlUseCase: FormatHtmlUseCase,
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    private val undoStack = mutableListOf<String>()
    private val redoStack = mutableListOf<String>()

    fun loadFile(fileId: String) {
        viewModelScope.launch {
            val file = projectRepository.getFile(fileId)
            if (file != null) {
                _uiState.update {
                    it.copy(currentFile = file, editedContent = file.content, isModified = false)
                }
                undoStack.clear()
                redoStack.clear()
                undoStack.add(file.content)
                updateUndoRedoState()
            }
        }
    }

    fun onContentChanged(newContent: String) {
        val current = _uiState.value.editedContent
        if (newContent != current) {
            redoStack.clear()
            undoStack.add(current)
            if (undoStack.size > 50) undoStack.removeAt(0)
            _uiState.update {
                it.copy(editedContent = newContent, isModified = true)
            }
            updateUndoRedoState()
        }
    }

    fun undo() {
        if (undoStack.size > 1) {
            redoStack.add(undoStack.removeAt(undoStack.lastIndex))
            val previous = undoStack.last()
            _uiState.update { it.copy(editedContent = previous) }
            updateUndoRedoState()
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val next = redoStack.removeAt(redoStack.lastIndex)
            undoStack.add(next)
            _uiState.update { it.copy(editedContent = next) }
            updateUndoRedoState()
        }
    }

    fun saveFile() {
        viewModelScope.launch {
            _uiState.value.currentFile?.let { file ->
                _uiState.update { it.copy(isSaving = true) }
                try {
                    saveFileUseCase(file.copy(content = _uiState.value.editedContent))
                    _uiState.update {
                        it.copy(isSaving = false, isModified = false, saveMessage = "保存成功")
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(isSaving = false, error = e.message) }
                }
            }
        }
    }

    fun formatCode() {
        val formatted = formatHtmlUseCase(_uiState.value.editedContent)
        _uiState.update { it.copy(editedContent = formatted, isModified = true) }
        redoStack.clear()
        undoStack.add(formatted)
        updateUndoRedoState()
    }

    fun clearSaveMessage() {
        _uiState.update { it.copy(saveMessage = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun updateUndoRedoState() {
        _uiState.update {
            it.copy(
                undoRedo = UndoRedoState(
                    canUndo = undoStack.size > 1,
                    canRedo = redoStack.isNotEmpty(),
                    currentContent = it.editedContent
                )
            )
        }
    }
}
