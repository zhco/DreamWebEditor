package com.dreamweb.editor.data.repository

import com.dreamweb.editor.domain.model.Snippet
import com.dreamweb.editor.domain.model.SnippetCategory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SnippetRepository @Inject constructor() {

    fun getAllSnippets(): List<Snippet> = snippets

    fun getSnippetsByCategory(category: SnippetCategory): List<Snippet> =
        snippets.filter { it.category == category }

    companion object {
        private val snippets = listOf(
            // Structure
            Snippet("s1", "HTML5 模板", "标准 HTML5 文档模板", """<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>文档标题</title>
</head>
<body>
    
</body>
</html>""", SnippetCategory.STRUCTURE),

            Snippet("s2", "响应式布局", "Flexbox 响应式容器", """<div style="display: flex; flex-wrap: wrap; gap: 1rem;">
    <div style="flex: 1; min-width: 200px;">列 1</div>
    <div style="flex: 1; min-width: 200px;">列 2</div>
    <div style="flex: 1; min-width: 200px;">列 3</div>
</div>""", SnippetCategory.STRUCTURE),

            Snippet("s3", "Grid 布局", "CSS Grid 三列布局", """<div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 1rem;">
    <div>项目 1</div>
    <div>项目 2</div>
    <div>项目 3</div>
</div>""", SnippetCategory.STRUCTURE),

            // Text
            Snippet("s4", "标题组合", "H1-H3 标题", """<h1>一级标题</h1>
<h2>二级标题</h2>
<h3>三级标题</h3>""", SnippetCategory.TEXT),

            Snippet("s5", "引用块", "Blockquote 引用", """<blockquote style="border-left: 4px solid #6200EE; padding: 1rem; background: #f5f5f5; margin: 1rem 0;">
    <p>这是一段引用文本。</p>
    <cite>—— 引用来源</cite>
</blockquote>""", SnippetCategory.TEXT),

            // List
            Snippet("s6", "无序列表", "ul > li 列表", """<ul>
    <li>列表项 1</li>
    <li>列表项 2</li>
    <li>列表项 3</li>
</ul>""", SnippetCategory.LIST),

            Snippet("s7", "有序列表", "ol > li 编号列表", """<ol>
    <li>第一步</li>
    <li>第二步</li>
    <li>第三步</li>
</ol>""", SnippetCategory.LIST),

            // Table
            Snippet("s8", "数据表格", "标准 HTML 表格", """<table style="width: 100%; border-collapse: collapse;">
    <thead>
        <tr style="background: #6200EE; color: white;">
            <th style="padding: 0.5rem;">列 1</th>
            <th style="padding: 0.5rem;">列 2</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td style="padding: 0.5rem; border-bottom: 1px solid #ddd;">数据 A</td>
            <td style="padding: 0.5rem; border-bottom: 1px solid #ddd;">数据 B</td>
        </tr>
    </tbody>
</table>""", SnippetCategory.TABLE),

            // Form
            Snippet("s9", "联系表单", "基本联系方式表单", """<form style="max-width: 400px;">
    <div style="margin-bottom: 1rem;">
        <label>姓名：</label>
        <input type="text" style="width: 100%; padding: 0.5rem; border: 1px solid #ddd; border-radius: 4px;">
    </div>
    <div style="margin-bottom: 1rem;">
        <label>邮箱：</label>
        <input type="email" style="width: 100%; padding: 0.5rem; border: 1px solid #ddd; border-radius: 4px;">
    </div>
    <div style="margin-bottom: 1rem;">
        <label>留言：</label>
        <textarea rows="4" style="width: 100%; padding: 0.5rem; border: 1px solid #ddd; border-radius: 4px;"></textarea>
    </div>
    <button type="submit" style="padding: 0.5rem 1.5rem; background: #6200EE; color: white; border: none; border-radius: 4px; cursor: pointer;">提交</button>
</form>""", SnippetCategory.FORM),

            Snippet("s10", "搜索框", "搜索输入框", """<div style="display: flex; gap: 0;">
    <input type="search" placeholder="搜索..." style="flex: 1; padding: 0.5rem; border: 1px solid #ddd; border-radius: 4px 0 0 4px;">
    <button style="padding: 0.5rem 1rem; background: #6200EE; color: white; border: none; border-radius: 0 4px 4px 0; cursor: pointer;">搜索</button>
</div>""", SnippetCategory.FORM),

            // Media
            Snippet("s11", "图片卡片", "带标题的图片卡片", """<figure style="margin: 1rem 0; text-align: center;">
    <img src="https://via.placeholder.com/400x200" alt="描述" style="max-width: 100%; border-radius: 8px;">
    <figcaption style="margin-top: 0.5rem; color: #666;">图片说明文字</figcaption>
</figure>""", SnippetCategory.MEDIA),

            Snippet("s12", "视频嵌入", "响应式视频容器", """<div style="position: relative; padding-bottom: 56.25%; height: 0; overflow: hidden;">
    <iframe src="https://www.youtube.com/embed/dQw4w9WgXcQ" 
            style="position: absolute; top: 0; left: 0; width: 100%; height: 100%;" 
            frameborder="0" allowfullscreen></iframe>
</div>""", SnippetCategory.MEDIA),

            // Meta
            Snippet("s13", "SEO Meta", "基础 SEO 元标签", """<meta name="description" content="页面描述">
<meta name="keywords" content="关键词1, 关键词2">
<meta name="author" content="作者">
<meta property="og:title" content="页面标题">
<meta property="og:description" content="页面描述">
<meta property="og:image" content="https://example.com/image.jpg">""", SnippetCategory.META),

            Snippet("s14", "Favicon", "网站图标设置", """<link rel="icon" type="image/png" sizes="32x32" href="/favicon-32x32.png">
<link rel="icon" type="image/png" sizes="16x16" href="/favicon-16x16.png">
<link rel="apple-touch-icon" sizes="180x180" href="/apple-touch-icon.png">""", SnippetCategory.META)
        )
    }
}
