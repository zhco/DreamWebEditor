package com.dreamweb.editor.ui.editor

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.dreamweb.editor.domain.model.FileType
import com.dreamweb.editor.domain.model.ProjectFile
import com.dreamweb.editor.ui.components.*
import com.dreamweb.editor.ui.preview.PreviewViewModel
import com.dreamweb.editor.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    projectId: String,
    onNavigateBack: () -> Unit,
    editorViewModel: EditorViewModel = hiltViewModel(),
    previewViewModel: PreviewViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedFileId by remember { mutableStateOf<String?>(null) }
    var files by remember { mutableStateOf<List<ProjectFile>>(emptyList()) }
    var showSnippetSheet by remember { mutableStateOf(false) }
    var showTagSheet by remember { mutableStateOf(false) }
    var showNewFileDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }

    val editorState by editorViewModel.uiState.collectAsState()
    val previewState by previewViewModel.uiState.collectAsState()

    // Load preview on first composition
    LaunchedEffect(projectId) {
        previewViewModel.loadProject(projectId)
    }

    val tabs = listOf("编辑", "预览", "文件")

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("DreamWeb Editor") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, "返回")
                        }
                    },
                    actions = {
                        if (selectedTab == 0) {
                            IconButton(onClick = { showTagSheet = true }) {
                                Icon(Icons.Default.Code, "插入标签")
                            }
                            IconButton(onClick = { showSnippetSheet = true }) {
                                Icon(Icons.Default.Widgets, "代码片段")
                            }
                            IconButton(onClick = { editorViewModel.formatCode() }) {
                                Icon(Icons.Default.FormatAlignLeft, "格式化")
                            }
                            IconButton(onClick = { editorViewModel.undo() },
                                enabled = editorState.undoRedo.canUndo) {
                                Icon(Icons.Default.Undo, "撤销")
                            }
                            IconButton(onClick = { editorViewModel.redo() },
                                enabled = editorState.undoRedo.canRedo) {
                                Icon(Icons.Default.Redo, "重做")
                            }
                            IconButton(onClick = { editorViewModel.saveFile() }) {
                                Icon(Icons.Default.Save, "保存")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
                // Tab row
                TabRow(selectedTabIndex = selectedTab) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> CodeEditorTab(
                    editorState = editorState,
                    onContentChanged = { editorViewModel.onContentChanged(it) },
                    selectedFileId = selectedFileId,
                    files = files,
                    onFileSelect = { fileId ->
                        selectedFileId = fileId
                        editorViewModel.loadFile(fileId)
                    }
                )
                1 -> PreviewTab(previewState = previewState)
                2 -> FilesTab(
                    files = files,
                    selectedFileId = selectedFileId,
                    onFileSelect = { fileId ->
                        selectedFileId = fileId
                        editorViewModel.loadFile(fileId)
                    },
                    onNewFile = { showNewFileDialog = true },
                    onDeleteFile = { showDeleteDialog = it }
                )
            }
        }
    }

    // Bottom sheets
    if (showSnippetSheet) {
        ModalBottomSheet(onDismissRequest = { showSnippetSheet = false }) {
            SnippetBottomSheet(
                onDismiss = { showSnippetSheet = false },
                onSnippetSelected = { code ->
                    val current = editorState.editedContent
                    editorViewModel.onContentChanged(current + "\n" + code)
                    showSnippetSheet = false
                }
            )
        }
    }

    if (showTagSheet) {
        ModalBottomSheet(onDismissRequest = { showTagSheet = false }) {
            TagInsertSheet(
                onDismiss = { showTagSheet = false },
                onTagSelected = { code ->
                    val current = editorState.editedContent
                    editorViewModel.onContentChanged(current + "\n" + code)
                    showTagSheet = false
                }
            )
        }
    }

    // Dialogs
    if (showNewFileDialog) {
        NewFileDialog(
            onDismiss = { showNewFileDialog = false },
            onConfirm = { name, type ->
                // TODO: Create file via repository
                showNewFileDialog = false
            }
        )
    }

    showDeleteDialog?.let { fileId ->
        val file = files.find { it.id == fileId }
        DeleteConfirmDialog(
            fileName = file?.name ?: "",
            onDismiss = { showDeleteDialog = null },
            onConfirm = {
                showDeleteDialog = null
            }
        )
    }

    // Save snackbar
    editorState.saveMessage?.let { msg ->
        LaunchedEffect(msg) {
            kotlinx.coroutines.delay(2000)
            editorViewModel.clearSaveMessage()
        }
    }
}

@Composable
fun CodeEditorTab(
    editorState: EditorUiState,
    onContentChanged: (String) -> Unit,
    selectedFileId: String?,
    files: List<ProjectFile>,
    onFileSelect: (String) -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background == BackgroundDark

    Column(modifier = Modifier.fillMaxSize()) {
        // File tabs row
        if (files.isNotEmpty()) {
            ScrollableTabRow(
                selectedTabIndex = files.indexOfFirst { it.id == selectedFileId }.coerceAtLeast(0),
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 0.dp
            ) {
                files.forEach { file ->
                    Tab(
                        selected = file.id == selectedFileId,
                        onClick = { onFileSelect(file.id) },
                        text = {
                            Text(
                                file.name,
                                maxLines = 1,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }
        }

        // Code editor
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            BasicTextField(
                value = editorState.editedContent,
                onValueChange = onContentChanged,
                modifier = Modifier.fillMaxSize(),
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    color = if (isDark) OnSurfaceDark else OnSurfaceLight,
                    lineHeight = 20.sp
                ),
                cursorBrush = SolidColor(
                    if (isDark) EditorCursorDark else EditorCursorLight
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        if (editorState.editedContent.isEmpty()) {
                            Text(
                                "在此输入 HTML 代码...",
                                color = MaterialTheme.colorScheme.outline,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}

@Composable
fun PreviewTab(previewState: com.dreamweb.editor.ui.preview.PreviewUiState) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (previewState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.allowFileAccess = true
                        webViewClient = WebViewClient()
                        loadDataWithBaseURL(
                            null,
                            previewState.previewHtml,
                            "text/html",
                            "UTF-8",
                            null
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun FilesTab(
    files: List<ProjectFile>,
    selectedFileId: String?,
    onFileSelect: (String) -> Unit,
    onNewFile: () -> Unit,
    onDeleteFile: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Add file button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "项目文件",
                style = MaterialTheme.typography.titleMedium
            )
            FilledTonalButton(onClick = onNewFile) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("新建")
            }
        }

        Divider()

        if (files.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("暂无文件", color = MaterialTheme.colorScheme.outline)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(files) { file ->
                    FileItem(
                        file = file,
                        isSelected = file.id == selectedFileId,
                        onClick = { onFileSelect(file.id) },
                        onDelete = { onDeleteFile(file.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun FileItem(
    file: ProjectFile,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val icon = when (file.type) {
        FileType.HTML -> Icons.Default.Html
        FileType.CSS -> Icons.Default.Style
        FileType.JAVASCRIPT -> Icons.Default.Javascript
    }
    val typeColor = when (file.type) {
        FileType.HTML -> MaterialTheme.colorScheme.primary
        FileType.CSS -> MaterialTheme.colorScheme.secondary
        FileType.JAVASCRIPT -> MaterialTheme.colorScheme.tertiary
    }

    Surface(
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = typeColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(file.name, style = MaterialTheme.typography.bodyLarge)
                Text(
                    file.type.extension,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
    Divider()
}
