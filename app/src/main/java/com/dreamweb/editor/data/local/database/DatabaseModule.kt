package com.dreamweb.editor.data.local.database

import android.content.Context
import androidx.room.Room
import com.dreamweb.editor.data.local.dao.ProjectDao
import com.dreamweb.editor.data.local.dao.ProjectFileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "dreamweb_editor.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideProjectDao(database: AppDatabase): ProjectDao = database.projectDao()

    @Provides
    fun provideProjectFileDao(database: AppDatabase): ProjectFileDao = database.projectFileDao()
}
