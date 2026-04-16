package com.digitguard.app.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.digitguard.app.ui.sos.SosHelper
import com.digitguard.app.ui.theme.PrimaryBlue
import com.digitguard.app.ui.theme.SafeGreen
import com.digitguard.app.ui.theme.SosRed
import com.digitguard.app.ui.theme.WarningRed

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    when {
        uiState.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("보호 상태 확인 중...", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
        uiState.errorMessage != null -> {
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
        else -> {
            HomeContent(uiState = uiState, onSosClick = {
                SosHelper.callGuardian(context, "")
            })
        }
    }
}

@Composable
private fun HomeContent(uiState: HomeUiState, onSosClick: () -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))

            Icon(
                imageVector = if (uiState.isSafe) Icons.Default.Shield else Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = if (uiState.isSafe) SafeGreen else WarningRed,
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (uiState.isSafe) "안전합니다" else "주의가 필요합니다",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                color = if (uiState.isSafe) SafeGreen else WarningRed,
            )

            if (uiState.guardianName != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "보호자: ${uiState.guardianName}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("오늘 차단", "${uiState.todayBlockedCount}건", Icons.Default.Block, Modifier.weight(1f))
                StatCard("보호 패턴", "${uiState.totalBlockedCount}개", Icons.Default.Security, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (uiState.recentThreats.isNotEmpty()) {
            item {
                Text("최근 차단 내역", style = MaterialTheme.typography.titleLarge, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(12.dp))
            }
            items(uiState.recentThreats) { threat ->
                ThreatCard(threat)
                Spacer(modifier = Modifier.height(8.dp))
            }
        } else {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(Icons.Default.CheckCircle, null, tint = SafeGreen, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("최근 감지된 위험이 없습니다", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onSosClick,
                modifier = Modifier.size(140.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = SosRed),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Phone, null, modifier = Modifier.size(36.dp))
                    Text("긴급 연락", fontSize = 20.sp)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = PrimaryBlue, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleLarge)
            Text(title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ThreatCard(threat: ThreatInfo) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Warning, null,
                tint = if (threat.level == "high") WarningRed else PrimaryBlue,
                modifier = Modifier.size(32.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(threat.description, style = MaterialTheme.typography.bodyLarge)
                Text(threat.time, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Surface(shape = MaterialTheme.shapes.small, color = SafeGreen.copy(alpha = 0.1f)) {
                Text("차단됨", modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), color = SafeGreen, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
