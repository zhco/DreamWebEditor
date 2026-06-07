package com.dreamweb.editor.domain.model

import java.util.UUID

data class ProjectFile(
    val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val name: String,
    val content: String = "",
    val type: FileType = FileType.HTML,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isOpen: Boolean = false
)

enum class FileType(val extension: String, val mimeType: String) {
    HTML(".html", "text/html"),
    CSS(".css", "text/css"),
    JAVASCRIPT(".js", "text/javascript");

    companion object {
        fun fromExtension(ext: String): FileType = when (ext.lowercase()) {
            ".css" -> CSS
            ".js" -> JAVASCRIPT
            else -> HTML
        }

        fun fromFileName(name: String): FileType {
            val ext = name.substringAfterLast('.', "")
            return fromExtension(".$ext")
        }
    }
}
