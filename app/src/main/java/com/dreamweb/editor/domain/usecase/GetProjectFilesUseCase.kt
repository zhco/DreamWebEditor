package com.dreamweb.editor.domain.usecase

import com.dreamweb.editor.data.repository.ProjectRepository
import com.dreamweb.editor.domain.model.ProjectFile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProjectFilesUseCase @Inject constructor(
    private val repository: ProjectRepository
) {
    operator fun invoke(projectId: String): Flow<List<ProjectFile>> =
        repository.getProjectFiles(projectId)
}
