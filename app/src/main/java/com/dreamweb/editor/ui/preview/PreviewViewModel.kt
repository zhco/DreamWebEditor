package com.dreamweb.editor.ui.preview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreamweb.editor.domain.model.ProjectFile
import com.dreamweb.editor.domain.usecase.GeneratePreviewUseCase
import com.dreamweb.editor.data.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PreviewUiState(
    val previewHtml: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class PreviewViewModel @Inject constructor(
    private val generatePreviewUseCase: GeneratePreviewUseCase,
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PreviewUiState())
    val uiState: StateFlow<PreviewUiState> = _uiState.asStateFlow()

    private var projectId: String = ""

    fun loadProject(projectId: String) {
        this.projectId = projectId
        refreshPreview()
    }

    fun refreshPreview() {
        if (projectId.isEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                projectRepository.getProjectFiles(projectId).first().let { files ->
                    val html = generatePreviewUseCase(files)
                    _uiState.update { it.copy(previewHtml = html, isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun updatePreviewWithFiles(files: List<ProjectFile>) {
        try {
            val html = generatePreviewUseCase(files)
            _uiState.update { it.copy(previewHtml = html) }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message) }
        }
    }
}
