package com.dreamweb.editor.ui.preview

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.dreamweb.editor.domain.model.FileType
import com.dreamweb.editor.domain.model.ProjectFile

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PreviewScreen(
    files: List<ProjectFile>,
    currentFile: ProjectFile?
) {
    var refreshTrigger by remember { mutableStateOf(0) }
    var webView by remember { mutableStateOf<WebView?>(null) }

    if (files.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "请先打开项目",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val previewHtml = remember(files, currentFile?.content, refreshTrigger) {
        generatePreview(files, currentFile)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Preview toolbar
        Surface(
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "预览",
                    style = MaterialTheme.typography.titleSmall
                )
                Row {
                    Text(
                        text = "${files.size} 个文件",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = {
                        webView?.reload()
                        refreshTrigger++
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "刷新预览")
                    }
                }
            }
        }

        // Preview WebView
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        allowFileAccess = true
                        setSupportZoom(true)
                        builtInZoomControls = true
                        displayZoomControls = false
                        useWideViewPort = true
                        loadWithOverviewMode = true
                    }
                    webChromeClient = WebChromeClient()
                    webViewClient = WebViewClient()
                    webView = this
                    loadDataWithBaseURL(
                        null,
                        previewHtml,
                        "text/html",
                        "UTF-8",
                        null
                    )
                }
            },
            update = { view ->
                if (refreshTrigger > 0) {
                    view.loadDataWithBaseURL(
                        null,
                        previewHtml,
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

private fun generatePreview(files: List<ProjectFile>, currentFile: ProjectFile?): String {
    val htmlFile = files.find { it.type == FileType.HTML }
        ?: files.firstOrNull()
        ?: return "<html><body><p>无内容</p></body></html>"

    val cssContent = files.filter { it.type == FileType.CSS }
        .joinToString("\n") { it.content }
    val jsContent = files.filter { it.type == FileType.JAVASCRIPT }
        .joinToString("\n") { it.content }

    var html = htmlFile.content

    // Inject CSS
    if (cssContent.isNotBlank()) {
        val styleTag = "\n<style>\n$cssContent\n</style>"
        html = if (html.contains("</head>")) {
            html.replace("</head>", "$styleTag\n</head>")
        } else if (html.contains("<body")) {
            html.replace("<body", "<head>$styleTag</head><body")
        } else {
            "<head>$styleTag</head>$html"
        }
    }

    // Inject JS
    if (jsContent.isNotBlank()) {
        val scriptTag = "\n<script>\n$jsContent\n</script>"
        html = if (html.contains("</body>")) {
            html.replace("</body>", "$scriptTag\n</body>")
        } else {
            "$html\n$scriptTag"
        }
    }

    return html
}
