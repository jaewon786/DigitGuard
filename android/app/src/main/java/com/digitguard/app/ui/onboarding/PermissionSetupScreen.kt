package com.digitguard.app.ui.onboarding

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digitguard.app.ui.theme.PrimaryBlue
import com.digitguard.app.ui.theme.SafeGreen

data class PermissionItem(
    val title: String,
    val description: String,
    val isGranted: Boolean,
    val onRequest: (Context) -> Unit,
)

@Composable
fun PermissionSetupScreen(
    onComplete: () -> Unit,
) {
    val context = LocalContext.current

    // 권한 상태 (실제로는 런타임에서 확인 필요)
    var accessibilityGranted by remember { mutableStateOf(false) }
    var overlayGranted by remember { mutableStateOf(false) }
    var notificationGranted by remember { mutableStateOf(false) }

    val permissions = listOf(
        PermissionItem(
            title = "접근성 서비스",
            description = "화면의 허위 광고를 감지합니다",
            isGranted = accessibilityGranted,
            onRequest = {
                it.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
                accessibilityGranted = true
            },
        ),
        PermissionItem(
            title = "화면 위에 표시",
            description = "위험 감지 시 경고를 표시합니다",
            isGranted = overlayGranted,
            onRequest = {
                it.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
                overlayGranted = true
            },
        ),
        PermissionItem(
            title = "알림 권한",
            description = "보호자에게 알림을 보냅니다",
            isGranted = notificationGranted,
            onRequest = {
                // Android 13+ POST_NOTIFICATIONS 런타임 요청
                notificationGranted = true
            },
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "권한 설정",
            style = MaterialTheme.typography.headlineLarge,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "안전한 보호를 위해\n아래 권한이 필요합니다",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(32.dp))

        permissions.forEach { permission ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                onClick = {
                    if (!permission.isGranted) {
                        permission.onRequest(context)
                    }
                },
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = if (permission.isGranted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = if (permission.isGranted) SafeGreen else MaterialTheme.colorScheme.outline,
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = permission.title,
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Text(
                            text = permission.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onComplete,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SafeGreen),
        ) {
            Text("설정 완료", fontSize = 22.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
