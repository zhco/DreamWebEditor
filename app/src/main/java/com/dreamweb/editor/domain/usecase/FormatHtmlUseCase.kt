package com.dreamweb.editor.domain.usecase

import javax.inject.Inject

class FormatHtmlUseCase @Inject constructor() {
    operator fun invoke(html: String): String {
        val sb = StringBuilder()
        var indent = 0
        val indentStr = "    "
        var i = 0

        while (i < html.length) {
            val ch = html[i]

            when {
                // Self-closing tags
                html.startsWith("<br", i, ignoreCase = true) ||
                html.startsWith("<hr", i, ignoreCase = true) ||
                html.startsWith("<img", i, ignoreCase = true) ||
                html.startsWith("<input", i, ignoreCase = true) ||
                html.startsWith("<meta", i, ignoreCase = true) ||
                html.startsWith("<link", i, ignoreCase = true) -> {
                    sb.append("\n")
                    sb.append(indentStr.repeat(indent))
                    while (i < html.length && html[i] != '>') {
                        sb.append(html[i])
                        i++
                    }
                    sb.append('>')
                    i++ // skip >
                }
                // Closing tag
                ch == '<' && i + 1 < html.length && html[i + 1] == '/' -> {
                    indent = maxOf(0, indent - 1)
                    sb.append("\n")
                    sb.append(indentStr.repeat(indent))
                    while (i < html.length && html[i] != '>') {
                        sb.append(html[i])
                        i++
                    }
                    sb.append('>')
                    i++
                }
                // Opening tag
                ch == '<' && i + 1 < html.length && html[i + 1] != '!' && html[i + 1] != '?' -> {
                    val tagEnd = html.indexOf('>', i)
                    if (tagEnd > 0) {
                        val tagContent = html.substring(i + 1, tagEnd).split(" ")[0]
                        val isBlock = tagContent.lowercase() in listOf(
                            "html", "head", "body", "div", "section", "article",
                            "header", "footer", "nav", "main", "aside",
                            "ul", "ol", "li", "table", "thead", "tbody", "tr", "td", "th",
                            "form", "fieldset", "p", "h1", "h2", "h3", "h4", "h5", "h6",
                            "script", "style", "pre", "blockquote"
                        )
                        if (isBlock) {
                            sb.append("\n")
                            sb.append(indentStr.repeat(indent))
                        }
                    }
                    sb.append(ch)
                    i++
                    while (i < html.length && html[i] != '>') {
                        sb.append(html[i])
                        i++
                    }
                    sb.append('>')
                    i++
                    if (tagEnd > 0) {
                        val tagName = html.substring(i - tagEnd + i - 1, tagEnd)
                            .let { if (it.contains(" ")) it.split(" ")[0] else it }
                        val voidElements = setOf("br", "hr", "img", "input", "meta", "link", "area", "base", "col", "embed", "source", "track", "wbr")
                        if (tagName.lowercase() !in voidElements && !html.substring(tagEnd - 1, tagEnd).contains("/")) {
                            indent++
                        }
                    }
                }
                else -> {
                    sb.append(ch)
                    i++
                }
            }
        }
        return sb.toString().trimStart()
    }
}
