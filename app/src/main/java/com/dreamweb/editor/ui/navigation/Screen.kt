package com.dreamweb.editor.ui.navigation

sealed class Screen(val route: String) {
    data object ProjectList : Screen("project_list")
    data object Editor : Screen("editor/{projectId}") {
        fun createRoute(projectId: String) = "editor/$projectId"
    }
}
