package com.digitguard.app.ui.warning

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digitguard.app.ui.theme.PrimaryBlue
import com.digitguard.app.ui.theme.SafeGreen
import com.digitguard.app.ui.theme.WarningRed

@Composable
fun WarningOverlay(
    onDismiss: () -> Unit,
    onCallGuardian: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp),
        ) {
            Text(
                text = "주의하세요!",
                fontSize = 36.sp,
                color = WarningRed,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "이것은 거짓 광고입니다.\n절대 설치하지 마세요.",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SafeGreen),
            ) {
                Text("무시하고 돌아가기", fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onCallGuardian,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            ) {
                Text("보호자에게 전화하기", fontSize = 22.sp)
            }
        }
    }
}
