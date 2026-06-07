package com.dreamweb.editor.domain.model

data class Snippet(
    val id: String,
    val name: String,
    val description: String,
    val code: String,
    val category: SnippetCategory
)

enum class SnippetCategory(val displayName: String) {
    STRUCTURE("结构"),
    TEXT("文本"),
    LIST("列表"),
    TABLE("表格"),
    FORM("表单"),
    MEDIA("媒体"),
    META("元数据")
}
