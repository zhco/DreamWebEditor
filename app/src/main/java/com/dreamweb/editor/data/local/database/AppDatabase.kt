package com.dreamweb.editor.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dreamweb.editor.data.local.dao.ProjectDao
import com.dreamweb.editor.data.local.dao.ProjectFileDao
import com.dreamweb.editor.data.local.entity.ProjectEntity
import com.dreamweb.editor.data.local.entity.ProjectFileEntity

@Database(
    entities = [ProjectEntity::class, ProjectFileEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun projectFileDao(): ProjectFileDao
}
