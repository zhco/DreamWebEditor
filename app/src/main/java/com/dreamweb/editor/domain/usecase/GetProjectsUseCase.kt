package com.dreamweb.editor.domain.usecase

import com.dreamweb.editor.data.repository.ProjectRepository
import com.dreamweb.editor.domain.model.Project
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProjectsUseCase @Inject constructor(
    private val repository: ProjectRepository
) {
    operator fun invoke(): Flow<List<Project>> = repository.getAllProjects()
}
