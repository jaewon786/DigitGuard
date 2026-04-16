package com.digitguard.app.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.digitguard.app.ui.theme.PrimaryBlue

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val settings by viewModel.settings.collectAsState()

    val protectionLabels = listOf("낮음", "보통", "높음")
    val fontLabels = listOf("보통", "크게", "매우 크게")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        Text(text = "설정", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(24.dp))

        // 보호자 관리
        SettingsSection(title = "보호자 관리", icon = Icons.Default.People) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("연결된 보호자", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            if (settings.guardianPhone.isNotEmpty()) settings.guardianPhone else "연결되지 않음",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 보호 수준
        SettingsSection(title = "보호 수준", icon = Icons.Default.Shield) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("현재: ${protectionLabels[settings.protectionLevel]}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        protectionLabels.forEachIndexed { index, label ->
                            FilterChip(
                                selected = settings.protectionLevel == index,
                                onClick = { viewModel.setProtectionLevel(index) },
                                label = { Text(label, fontSize = 18.sp) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        when (settings.protectionLevel) {
                            0 -> "위험도 '높음'만 경고합니다"
                            1 -> "위험도 '중간' 이상 경고합니다"
                            else -> "모든 의심 활동을 경고합니다"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 알림 설정
        SettingsSection(title = "알림 설정", icon = Icons.Default.Notifications) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    SettingsToggle(
                        title = "경고 소리",
                        description = "위험 감지 시 소리로 알림",
                        checked = settings.soundEnabled,
                        onCheckedChange = { viewModel.setSoundEnabled(it) },
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    SettingsToggle(
                        title = "음성 안내",
                        description = "경고 내용을 음성으로 읽어줌",
                        checked = settings.ttsEnabled,
                        onCheckedChange = { viewModel.setTtsEnabled(it) },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 글씨 크기
        SettingsSection(title = "글씨 크기", icon = Icons.Default.TextFields) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("현재: ${fontLabels[settings.fontSizeLevel]}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        fontLabels.forEachIndexed { index, label ->
                            FilterChip(
                                selected = settings.fontSizeLevel == index,
                                onClick = { viewModel.setFontSizeLevel(index) },
                                label = { Text(label, fontSize = 18.sp) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "미리보기: 이 글씨 크기로 표시됩니다",
                        fontSize = when (settings.fontSizeLevel) {
                            0 -> 18.sp; 1 -> 22.sp; else -> 26.sp
                        },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 앱 정보
        SettingsSection(title = "앱 정보", icon = Icons.Default.Info) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    InfoRow("버전", "1.0.0")
                    InfoRow("사용자", settings.userName.ifEmpty { "미등록" })
                    InfoRow("역할", if (settings.userRole == "guardian") "보호자" else "피보호자")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SettingsSection(title: String, icon: ImageVector, content: @Composable () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
        Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.titleLarge)
    }
    content()
}

@Composable
private fun SettingsToggle(title: String, description: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
