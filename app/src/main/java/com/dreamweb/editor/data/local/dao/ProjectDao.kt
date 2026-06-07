package com.dreamweb.editor.data.local.dao

import androidx.room.*
import com.dreamweb.editor.data.local.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY updated_at DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: String): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProject(id: String)

    @Query("UPDATE projects SET file_count = (SELECT COUNT(*) FROM project_files WHERE project_id = :projectId), updated_at = :updatedAt WHERE id = :projectId")
    suspend fun updateFileCount(projectId: String, updatedAt: Long)
}
