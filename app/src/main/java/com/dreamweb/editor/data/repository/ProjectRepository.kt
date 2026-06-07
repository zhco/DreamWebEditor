package com.dreamweb.editor.data.repository

import com.dreamweb.editor.data.local.dao.ProjectDao
import com.dreamweb.editor.data.local.dao.ProjectFileDao
import com.dreamweb.editor.data.local.entity.ProjectEntity
import com.dreamweb.editor.data.local.entity.ProjectFileEntity
import com.dreamweb.editor.domain.model.FileType
import com.dreamweb.editor.domain.model.Project
import com.dreamweb.editor.domain.model.ProjectFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepository @Inject constructor(
    private val projectDao: ProjectDao,
    private val fileDao: ProjectFileDao
) {
    fun getAllProjects(): Flow<List<Project>> =
        projectDao.getAllProjects().map { entities ->
            entities.map { it.toDomain() }
        }

    suspend fun createProject(project: Project) {
        projectDao.insertProject(project.toEntity())
    }

    suspend fun deleteProject(projectId: String) {
        fileDao.deleteAllFilesInProject(projectId)
        projectDao.deleteProject(projectId)
    }

    fun getProjectFiles(projectId: String): Flow<List<ProjectFile>> =
        fileDao.getProjectFiles(projectId).map { entities ->
            entities.map { it.toDomain() }
        }

    suspend fun saveFile(file: ProjectFile) {
        val now = System.currentTimeMillis()
        val entity = file.copy(updatedAt = now).toEntity()
        fileDao.insertFile(entity)
        projectDao.updateFileCount(file.projectId, now)
    }

    suspend fun deleteFile(fileId: String) {
        fileDao.deleteFile(fileId)
    }

    suspend fun getFile(fileId: String): ProjectFile? =
        fileDao.getFileById(fileId)?.toDomain()

    private fun Project.toEntity() = ProjectEntity(
        id = id, name = name, createdAt = createdAt,
        updatedAt = updatedAt, fileCount = fileCount
    )

    private fun ProjectEntity.toDomain() = Project(
        id = id, name = name, createdAt = createdAt,
        updatedAt = updatedAt, fileCount = fileCount
    )

    private fun ProjectFile.toEntity() = ProjectFileEntity(
        id = id, projectId = projectId, name = name,
        content = content, type = type.extension,
        createdAt = createdAt, updatedAt = updatedAt,
        isOpen = isOpen
    )

    private fun ProjectFileEntity.toDomain() = ProjectFile(
        id = id, projectId = projectId, name = name,
        content = content, type = FileType.fromExtension(type),
        createdAt = createdAt, updatedAt = updatedAt,
        isOpen = isOpen
    )
}
