package com.dreamweb.editor.domain.usecase

import com.dreamweb.editor.domain.model.ProjectFile
import com.dreamweb.editor.domain.model.FileType
import javax.inject.Inject

class GeneratePreviewUseCase @Inject constructor() {
    operator fun invoke(files: List<ProjectFile>): String {
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
            if (html.contains("</head>")) {
                html = html.replace("</head>", "$styleTag\n</head>")
            } else if (html.contains("<body")) {
                html = html.replace("<body", "<head>$styleTag</head><body")
            } else {
                html = "<head>$styleTag</head>$html"
            }
        }

        // Inject JS
        if (jsContent.isNotBlank()) {
            val scriptTag = "\n<script>\n$jsContent\n</script>"
            if (html.contains("</body>")) {
                html = html.replace("</body>", "$scriptTag\n</body>")
            } else {
                html = "$html\n$scriptTag"
            }
        }

        return html
    }
}
