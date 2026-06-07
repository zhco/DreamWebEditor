package com.dreamweb.editor.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dreamweb.editor.domain.model.Project
import com.dreamweb.editor.domain.model.ProjectFile
import com.dreamweb.editor.ui.editor.EditorScreen
import com.dreamweb.editor.ui.preview.PreviewScreen
import com.dreamweb.editor.ui.project.ProjectListScreen

sealed class Screen(val route: String) {
    object ProjectList : Screen("project_list")
    object Editor : Screen("editor")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DreamWebNavHost(
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    navController: NavHostController = rememberNavController()
) {
    var currentProject by remember { mutableStateOf<Project?>(null) }
    var currentFiles by remember { mutableStateOf<List<ProjectFile>>(emptyList()) }
    var currentFile by remember { mutableStateOf<ProjectFile?>(null) }
    var selectedTab by remember { mutableStateOf(0) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            if (currentRoute == Screen.Editor.route && currentProject != null) {
                EditorTopBar(
                    projectName = currentProject!!.name,
                    fileName = currentFile?.name,
                    darkTheme = darkTheme,
                    onToggleTheme = onToggleTheme,
                    onBack = { navController.popBackStack() },
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.ProjectList.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.ProjectList.route) {
                ProjectListScreen(
                    onProjectSelected = { project ->
                        currentProject = project
                        navController.navigate(Screen.Editor.route)
                    }
                )
            }
            composable(Screen.Editor.route) {
                if (currentProject != null) {
                    when (selectedTab) {
                        0 -> EditorScreen(
                            file = currentFile,
                            onContentChange = { newContent ->
                                currentFile = currentFile?.copy(content = newContent)
                            },
                            onSave = { },
                            onFormat = { },
                            onUndo = { },
                            onRedo = { },
                            onInsertTag = { _, _, _ -> }
                        )
                        1 -> PreviewScreen(
                            files = currentFiles,
                            currentFile = currentFile
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorTopBar(
    projectName: String,
    fileName: String?,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onBack: () -> Unit,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = projectName,
                    style = MaterialTheme.typography.titleMedium
                )
                if (fileName != null) {
                    Text(
                        text = fileName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        navigationIcon = {
            TextButton(onClick = onBack) {
                Text("项目")
            }
        },
        actions = {
            TextButton(onClick = onToggleTheme) {
                Text(if (darkTheme) "浅色" else "深色")
            }
        }
    )
}
