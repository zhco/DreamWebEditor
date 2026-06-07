package com.dreamweb.editor.domain.usecase

import com.dreamweb.editor.data.repository.ProjectRepository
import com.dreamweb.editor.domain.model.ProjectFile
import javax.inject.Inject

class SaveFileUseCase @Inject constructor(
    private val repository: ProjectRepository
) {
    suspend operator fun invoke(file: ProjectFile) = repository.saveFile(file)
}
