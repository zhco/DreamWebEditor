package com.dreamweb.editor.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnippetBottomSheet(
    onDismiss: () -> Unit,
    onSnippetSelected: (String) -> Unit
) {
    val snippets = listOf(
        "HTML5 模板" to """<!DOCTYPE html>
<html lang="zh-CN">
<head><meta charset="UTF-8"><title></title></head>
<body></body>
</html>""",
        "响应式布局" to """<div style="display:flex;flex-wrap:wrap;gap:1rem">
  <div style="flex:1;min-width:200px">列1</div>
  <div style="flex:1;min-width:200px">列2</div>
</div>""",
        "Grid布局" to """<div style="display:grid;grid-template-columns:repeat(3,1fr);gap:1rem">
  <div>项目1</div><div>项目2</div><div>项目3</div>
</div>""",
        "标题H1-H3" to "<h1>标题</h1>\n<h2>副标题</h2>\n<h3>小标题</h3>",
        "引用块" to """<blockquote style="border-left:4px solid #6200EE;padding:1rem;background:#f5f5f5">
  <p>引用文本</p>
</blockquote>""",
        "无序列表" to "<ul>\n  <li>项目1</li>\n  <li>项目2</li>\n</ul>",
        "有序列表" to "<ol>\n  <li>步骤1</li>\n  <li>步骤2</li>\n</ol>",
        "数据表格" to """<table style="width:100%;border-collapse:collapse">
  <tr><th>列1</th><th>列2</th></tr>
  <tr><td>数据</td><td>数据</td></tr>
</table>""",
        "联系表单" to """<form>
  <input type="text" placeholder="姓名"><br>
  <input type="email" placeholder="邮箱"><br>
  <textarea placeholder="留言"></textarea><br>
  <button type="submit">提交</button>
</form>""",
        "图片卡片" to """<figure style="text-align:center">
  <img src="url" alt="描述" style="max-width:100%">
  <figcaption>图片说明</figcaption>
</figure>"""
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Text("插入代码片段", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))
        snippets.forEach { (name, code) ->
            TextButton(
                onClick = { onSnippetSelected(code) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(name, modifier = Modifier.fillMaxWidth())
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
            Text("取消")
        }
    }
}

@Composable
fun TagInsertSheet(
    onDismiss: () -> Unit,
    onTagSelected: (String) -> Unit
) {
    val tags = listOf(
        "链接 <a>" to """<a href="url">链接文本</a>""",
        "图片 <img>" to """<img src="url" alt="描述">""",
        "容器 <div>" to "<div>\n  \n</div>",
        "段落 <p>" to "<p></p>",
        "标题 H1" to "<h1></h1>",
        "标题 H2" to "<h2></h2>",
        "标题 H3" to "<h3></h3>",
        "行内 <span>" to "<span></span>",
        "换行 <br>" to "<br>",
        "分割线 <hr>" to "<hr>",
        "按钮 <button>" to "<button>按钮</button>",
        "输入框" to "<input type=\"text\" placeholder=\"\">"
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Text("插入 HTML 标签", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))
        tags.forEach { (name, code) ->
            TextButton(
                onClick = { onTagSelected(code) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(name, modifier = Modifier.fillMaxWidth())
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
            Text("取消")
        }
    }
}

@Composable
fun NewFileDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var fileName = ""
    var selectedType = "html"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("新建文件") },
        text = {
            Column {
                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("文件名") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    listOf("html" to "HTML", "css" to "CSS", "js" to "JS").forEach { (type, label) ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(label) },
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(fileName, selectedType) }) {
                Text("创建")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}

@Composable
fun DeleteConfirmDialog(
    fileName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("确认删除") },
        text = { Text("确定要删除 \"$fileName\" 吗？此操作不可撤销。") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("删除", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}
