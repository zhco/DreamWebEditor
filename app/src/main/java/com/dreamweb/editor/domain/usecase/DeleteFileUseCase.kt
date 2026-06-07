package com.dreamweb.editor.domain.usecase

import com.dreamweb.editor.data.repository.ProjectRepository
import javax.inject.Inject

class DeleteFileUseCase @Inject constructor(
    private val repository: ProjectRepository
) {
    suspend operator fun invoke(fileId: String) = repository.deleteFile(fileId)
}
