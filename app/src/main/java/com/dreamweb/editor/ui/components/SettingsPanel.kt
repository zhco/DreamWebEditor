package com.dreamweb.editor.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dreamweb.editor.domain.model.EditorSettings

@Composable
fun SettingsPanel(
    settings: EditorSettings,
    onSettingsChange: (EditorSettings) -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "编辑器设置",
            style = MaterialTheme.typography.titleLarge
        )

        // Font Size
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("字体大小: ${settings.fontSize}")
            Slider(
                value = settings.fontSize.toFloat(),
                onValueChange = { onSettingsChange(settings.copy(fontSize = it.toInt())) },
                valueRange = 10f..24f,
                steps = 13,
                modifier = Modifier.width(200.dp)
            )
        }

        // Tab Size
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Tab 大小: ${settings.tabSize}")
            Slider(
                value = settings.tabSize.toFloat(),
                onValueChange = { onSettingsChange(settings.copy(tabSize = it.toInt())) },
                valueRange = 2f..8f,
                steps = 5,
                modifier = Modifier.width(200.dp)
            )
        }

        Divider()

        // Toggles
        SettingToggle(
            label = "自动保存",
            checked = settings.isAutoSaveEnabled,
            onCheckedChange = { onSettingsChange(settings.copy(isAutoSaveEnabled = it)) }
        )

        SettingToggle(
            label = "实时预览",
            checked = settings.isLivePreviewEnabled,
            onCheckedChange = { onSettingsChange(settings.copy(isLivePreviewEnabled = it)) }
        )

        SettingToggle(
            label = "使用空格代替 Tab",
            checked = settings.useSoftTabs,
            onCheckedChange = { onSettingsChange(settings.copy(useSoftTabs = it)) }
        )

        SettingToggle(
            label = "自动换行",
            checked = settings.wordWrap,
            onCheckedChange = { onSettingsChange(settings.copy(wordWrap = it)) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onDismiss,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("完成")
        }
    }
}

@Composable
private fun SettingToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
