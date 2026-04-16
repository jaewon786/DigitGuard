package com.digitguard.app.ui.guardian

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
import androidx.compose.ui.unit.sp
import com.digitguard.app.ui.theme.PrimaryBlue
import com.digitguard.app.ui.theme.SafeGreen
import com.digitguard.app.ui.theme.SosRed
import com.digitguard.app.ui.theme.WarningRed

data class ProtectedUser(
    val id: String,
    val name: String,
    val status: String,
    val recentThreatsCount: Int,
    val pendingInstalls: Int,
    val lastChecked: String,
)

data class PendingInstall(
    val id: String,
    val appName: String,
    val packageName: String,
    val riskScore: Int,
)

@Composable
fun GuardianDashboardScreen() {
    var selectedUser by remember { mutableStateOf<ProtectedUser?>(null) }
    var showWhitelist by remember { mutableStateOf(false) }

    val protectedUsers = remember {
        listOf(
            ProtectedUser("1", "어머니", "safe", 0, 0, "방금 전"),
            ProtectedUser("2", "아버지", "warning", 3, 1, "5분 전"),
        )
    }

    if (showWhitelist && selectedUser != null) {
        WhitelistManagementView(
            userName = selectedUser!!.name,
            onBack = { showWhitelist = false },
        )
    } else if (selectedUser != null) {
        ProtectedUserDetailView(
            user = selectedUser!!,
            onBack = { selectedUser = null },
            onWhitelistClick = { showWhitelist = true },
        )
    } else {
        DashboardListView(
            users = protectedUsers,
            onUserClick = { selectedUser = it },
        )
    }
}

@Composable
private fun DashboardListView(
    users: List<ProtectedUser>,
    onUserClick: (ProtectedUser) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
    ) {
        Text("보호자 대시보드", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "피보호자의 보호 상태를 확인합니다",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(20.dp))

        // 연결 코드 발급 버튼
        OutlinedButton(
            onClick = { /* TODO: 연결 코드 발급 API 호출 */ },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("새 피보호자 연결하기", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(users) { user ->
                ProtectedUserCard(user = user, onClick = { onUserClick(user) })
            }
        }
    }
}

@Composable
private fun ProtectedUserCard(user: ProtectedUser, onClick: () -> Unit) {
    val isSafe = user.status == "safe"

    Card(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Person, contentDescription = null,
                    modifier = Modifier.size(40.dp), tint = PrimaryBlue,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(user.name, style = MaterialTheme.typography.titleLarge)
                    Text(
                        "마지막 확인: ${user.lastChecked}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Icon(
                    if (isSafe) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (isSafe) SafeGreen else WarningRed,
                    modifier = Modifier.size(32.dp),
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatChip("위험 감지 ${user.recentThreatsCount}건", user.recentThreatsCount > 0)
                StatChip("설치 대기 ${user.pendingInstalls}건", user.pendingInstalls > 0)
            }
        }
    }
}

@Composable
private fun StatChip(text: String, isWarning: Boolean) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = if (isWarning) WarningRed.copy(alpha = 0.1f) else SafeGreen.copy(alpha = 0.1f),
    ) {
        Text(
            text, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = if (isWarning) WarningRed else SafeGreen,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun ProtectedUserDetailView(
    user: ProtectedUser,
    onBack: () -> Unit,
    onWhitelistClick: () -> Unit,
) {
    val pendingInstalls = remember {
        listOf(
            PendingInstall("1", "수상한 청소앱", "com.suspicious.cleaner", 75),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
    ) {
        TextButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("뒤로", style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("${user.name} 보호 현황", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))

        // 관리 메뉴
        Card(modifier = Modifier.fillMaxWidth(), onClick = onWhitelistClick) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AppShortcut, contentDescription = null, tint = PrimaryBlue)
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("허용 앱 관리", style = MaterialTheme.typography.bodyLarge)
                    Text("화이트리스트 앱 목록 관리", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 설치 승인 대기
        if (pendingInstalls.isNotEmpty()) {
            Text("설치 승인 대기", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(12.dp))

            pendingInstalls.forEach { install ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(install.appName, style = MaterialTheme.typography.titleLarge)
                        Text(install.packageName, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            "위험도: ${install.riskScore}%",
                            color = if (install.riskScore > 50) WarningRed else SafeGreen,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = { /* TODO: 승인 API 호출 */ },
                                colors = ButtonDefaults.buttonColors(containerColor = SafeGreen),
                                modifier = Modifier.weight(1f),
                            ) { Text("승인", fontSize = 18.sp) }
                            Button(
                                onClick = { /* TODO: 거부 API 호출 */ },
                                colors = ButtonDefaults.buttonColors(containerColor = SosRed),
                                modifier = Modifier.weight(1f),
                            ) { Text("거부", fontSize = 18.sp) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WhitelistManagementView(
    userName: String,
    onBack: () -> Unit,
) {
    val whitelistApps = remember {
        mutableStateListOf(
            "카카오톡" to "com.kakao.talk",
            "네이버" to "com.nhn.android.search",
            "유튜브" to "com.google.android.youtube",
            "카카오맵" to "net.daum.android.map",
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
    ) {
        TextButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("뒤로", style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("${userName} 허용 앱", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "아래 앱은 설치 시 별도 승인 없이 허용됩니다",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(whitelistApps.size) { index ->
                val (name, pkg) = whitelistApps[index]
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SafeGreen)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(name, style = MaterialTheme.typography.bodyLarge)
                            Text(pkg, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { whitelistApps.removeAt(index) }) {
                            Icon(Icons.Default.Delete, contentDescription = "삭제", tint = SosRed)
                        }
                    }
                }
            }
        }
    }
}
