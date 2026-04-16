package com.digitguard.app.ui.education

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.digitguard.app.data.remote.api.EducationContentDto
import com.digitguard.app.ui.theme.PrimaryBlue
import com.digitguard.app.ui.theme.WarningRed

@Composable
fun EducationScreen(viewModel: EducationViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        uiState.errorMessage != null && uiState.contents.isEmpty() -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ErrorOutline, null, Modifier.size(64.dp), tint = WarningRed)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(uiState.errorMessage!!, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.refresh() }) { Text("다시 시도") }
                }
            }
        }
        uiState.selectedDetail != null -> {
            EducationDetailView(
                content = uiState.selectedDetail!!,
                onBack = { viewModel.clearDetail() },
            )
        }
        else -> {
            EducationListView(
                contents = uiState.contents,
                onItemClick = { viewModel.selectContent(it.id) },
            )
        }
    }
}

@Composable
private fun EducationListView(
    contents: List<EducationContentDto>,
    onItemClick: (EducationContentDto) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("배우기", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "사기를 예방하는 방법을 알아보세요",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(contents) { item ->
                Card(modifier = Modifier.fillMaxWidth(), onClick = { onItemClick(item) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            categoryIcon(item.category), null,
                            modifier = Modifier.size(48.dp), tint = PrimaryBlue,
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.title, style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(item.summary, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(Icons.Default.ChevronRight, null)
                    }
                }
            }
        }
    }
}

@Composable
private fun EducationDetailView(
    content: EducationContentDto,
    onBack: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        TextButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("뒤로", style = MaterialTheme.typography.bodyLarge)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Icon(categoryIcon(content.category), null, modifier = Modifier.size(64.dp), tint = PrimaryBlue)
        Spacer(modifier = Modifier.height(16.dp))
        Text(content.title, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val steps = content.steps ?: emptyList()
            items(steps.size) { index ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.Top) {
                        Surface(shape = MaterialTheme.shapes.small, color = PrimaryBlue, modifier = Modifier.size(36.dp)) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Text("${index + 1}", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelLarge)
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(steps[index], style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

private fun categoryIcon(category: String) = when (category) {
    "fake_ad" -> Icons.Default.BugReport
    "app_safety" -> Icons.Default.AppBlocking
    "voice_phishing" -> Icons.Default.PhoneDisabled
    "smishing" -> Icons.Default.Sms
    "password" -> Icons.Default.Lock
    else -> Icons.Default.MenuBook
}
