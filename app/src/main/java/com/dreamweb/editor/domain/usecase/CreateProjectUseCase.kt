package com.dreamweb.editor.domain.usecase

import com.dreamweb.editor.data.repository.ProjectRepository
import com.dreamweb.editor.domain.model.Project
import com.dreamweb.editor.domain.model.ProjectFile
import com.dreamweb.editor.domain.model.FileType
import javax.inject.Inject

class CreateProjectUseCase @Inject constructor(
    private val repository: ProjectRepository
) {
    suspend operator fun invoke(name: String): Project {
        val project = Project(name = name)
        repository.createProject(project)

        // Create default index.html
        val defaultHtml = ProjectFile(
            projectId = project.id,
            name = "index.html",
            content = """<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>我的网页</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <h1>Hello, DreamWeb!</h1>
    <p>欢迎使用 DreamWeb Editor 开始创建你的网页。</p>
    <script src="script.js"></script>
</body>
</html>""",
            type = FileType.HTML
        )
        repository.saveFile(defaultHtml)

        // Create default style.css
        val defaultCss = ProjectFile(
            projectId = project.id,
            name = "style.css",
            content = """* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body {
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
    line-height: 1.6;
    color: #333;
    max-width: 800px;
    margin: 0 auto;
    padding: 2rem;
}

h1 {
    color: #6200EE;
    margin-bottom: 1rem;
}

p {
    margin-bottom: 1rem;
}""",
            type = FileType.CSS
        )
        repository.saveFile(defaultCss)

        // Create default script.js
        val defaultJs = ProjectFile(
            projectId = project.id,
            name = "script.js",
            content = """// JavaScript 代码
console.log('DreamWeb Editor - 页面已加载');

document.addEventListener('DOMContentLoaded', () => {
    console.log('DOM 已就绪');
});""",
            type = FileType.JAVASCRIPT
        )
        repository.saveFile(defaultJs)

        return project
    }
}
