package com.dreamweb.editor.data.local.dao

import androidx.room.*
import com.dreamweb.editor.data.local.entity.ProjectFileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectFileDao {
    @Query("SELECT * FROM project_files WHERE project_id = :projectId ORDER BY type ASC, name ASC")
    fun getProjectFiles(projectId: String): Flow<List<ProjectFileEntity>>

    @Query("SELECT * FROM project_files WHERE id = :id")
    suspend fun getFileById(id: String): ProjectFileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: ProjectFileEntity)

    @Update
    suspend fun updateFile(file: ProjectFileEntity)

    @Query("DELETE FROM project_files WHERE id = :id")
    suspend fun deleteFile(id: String)

    @Query("DELETE FROM project_files WHERE project_id = :projectId")
    suspend fun deleteAllFilesInProject(projectId: String)
}
