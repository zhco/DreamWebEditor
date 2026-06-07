package com.dreamweb.editor.ui.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreamweb.editor.data.repository.ProjectRepository
import com.dreamweb.editor.data.repository.SnippetRepository
import com.dreamweb.editor.domain.model.FileType
import com.dreamweb.editor.domain.model.Project
import com.dreamweb.editor.domain.model.ProjectFile
import com.dreamweb.editor.domain.model.Snippet
import com.dreamweb.editor.domain.model.SnippetCategory
import com.dreamweb.editor.domain.usecase.CreateProjectUseCase
import com.dreamweb.editor.domain.usecase.DeleteFileUseCase
import com.dreamweb.editor.domain.usecase.GetProjectFilesUseCase
import com.dreamweb.editor.domain.usecase.GetProjectsUseCase
import com.dreamweb.editor.domain.usecase.SaveFileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val getProjectsUseCase: GetProjectsUseCase,
    private val getProjectFilesUseCase: GetProjectFilesUseCase,
    private val createProjectUseCase: CreateProjectUseCase,
    private val saveFileUseCase: SaveFileUseCase,
    private val deleteFileUseCase: DeleteFileUseCase,
    private val projectRepository: ProjectRepository,
    private val snippetRepository: SnippetRepository
) : ViewModel() {

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    private val _currentProject = MutableStateFlow<Project?>(null)
    val currentProject: StateFlow<Project?> = _currentProject.asStateFlow()

    private val _files = MutableStateFlow<List<ProjectFile>>(emptyList())
    val files: StateFlow<List<ProjectFile>> = _files.asStateFlow()

    private val _selectedFile = MutableStateFlow<ProjectFile?>(null)
    val selectedFile: StateFlow<ProjectFile?> = _selectedFile.asStateFlow()

    private val _events = MutableSharedFlow<ProjectEvent>()
    val events = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            getProjectsUseCase().collect { projectList ->
                _projects.value = projectList
            }
        }
    }

    fun createProject(name: String) {
        viewModelScope.launch {
            try {
                val project = createProjectUseCase(name)
                _currentProject.value = project
                _events.emit(ProjectEvent.ProjectCreated(project))
            } catch (e: Exception) {
                _events.emit(ProjectEvent.Error("创建项目失败: ${e.message}"))
            }
        }
    }

    fun openProject(project: Project) {
        _currentProject.value = project
        viewModelScope.launch {
            getProjectFilesUseCase(project.id).collect { fileList ->
                _files.value = fileList
            }
        }
    }

    fun closeProject() {
        _currentProject.value = null
        _files.value = emptyList()
        _selectedFile.value = null
    }

    fun selectFile(file: ProjectFile) {
        _selectedFile.value = file
    }

    fun createFile(name: String, type: FileType) {
        val project = _currentProject.value ?: return
        val file = ProjectFile(
            projectId = project.id,
            name = name,
            type = type,
            content = when (type) {
                FileType.HTML -> "<!DOCTYPE html>\n<html>\n<head>\n    <title></title>\n</head>\n<body>\n\n</body>\n</html>"
                FileType.CSS -> "/* CSS */\n"
                FileType.JAVASCRIPT -> "// JavaScript\n"
            }
        )
        viewModelScope.launch {
            saveFileUseCase(file)
            _events.emit(ProjectEvent.FileCreated(file))
        }
    }

    fun deleteFile(file: ProjectFile) {
        viewModelScope.launch {
            deleteFileUseCase(file.id)
            if (_selectedFile.value?.id == file.id) {
                _selectedFile.value = null
            }
            _events.emit(ProjectEvent.FileDeleted(file.name))
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            try {
                projectRepository.deleteProject(projectId)
                if (_currentProject.value?.id == projectId) {
                    closeProject()
                }
                _events.emit(ProjectEvent.ProjectDeleted(projectId))
            } catch (e: Exception) {
                _events.emit(ProjectEvent.Error("删除项目失败: ${e.message}"))
            }
        }
    }

    fun getSnippets(): List<Snippet> = snippetRepository.getAllSnippets()

    fun getSnippetsByCategory(category: SnippetCategory): List<Snippet> =
        snippetRepository.getSnippetsByCategory(category)
}

sealed class ProjectEvent {
    data class ProjectCreated(val project: Project) : ProjectEvent()
    data class ProjectDeleted(val projectId: String) : ProjectEvent()
    data class FileCreated(val file: ProjectFile) : ProjectEvent()
    data class FileDeleted(val name: String) : ProjectEvent()
    data class Error(val message: String) : ProjectEvent()
}
