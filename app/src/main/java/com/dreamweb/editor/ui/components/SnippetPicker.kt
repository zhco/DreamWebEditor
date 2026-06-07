package com.dreamweb.editor.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dreamweb.editor.domain.model.Snippet
import com.dreamweb.editor.domain.model.SnippetCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnippetPicker(
    snippets: List<Snippet>,
    onSnippetSelected: (Snippet) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf<SnippetCategory?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "代码片段",
            style = MaterialTheme.typography.titleLarge
        )

        // Category filter chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SnippetCategory.entries.forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = {
                        selectedCategory = if (selectedCategory == category) null else category
                    },
                    label = { Text(category.displayName) }
                )
            }
        }

        Divider()

        val filteredSnippets = if (selectedCategory != null) {
            snippets.filter { it.category == selectedCategory }
        } else {
            snippets
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(filteredSnippets) { snippet ->
                SnippetCard(snippet = snippet, onClick = { onSnippetSelected(snippet) })
            }
        }

        TextButton(onClick = onDismiss) {
            Text("取消")
        }
    }
}

@Composable
private fun SnippetCard(snippet: Snippet, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = snippet.name,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = snippet.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = snippet.code.take(100).replace("\n", " "),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(8.dp),
                    maxLines = 2
                )
            }
        }
    }
}
