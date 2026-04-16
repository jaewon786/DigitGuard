package com.digitguard.app.ui.guardian

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.digitguard.app.ui.theme.PrimaryBlue
import com.digitguard.app.ui.theme.SafeGreen

@Composable
fun GuardianLinkScreen(
    onLinked: () -> Unit,
    onSkip: () -> Unit,
    viewModel: GuardianLinkViewModel = hiltViewModel(),
) {
    var linkCode by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    // 연결 성공 시 다음 화면으로
    LaunchedEffect(uiState.isLinked) {
        if (uiState.isLinked) onLinked()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Icon(
            imageVector = Icons.Default.Link,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = PrimaryBlue,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "보호자 연결",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "보호자에게 받은\n연결 코드를 입력해 주세요",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(
            value = linkCode,
            onValueChange = {
                if (it.length <= 6) linkCode = it.uppercase()
            },
            label = { Text("연결 코드 (6자리)", fontSize = 18.sp) },
            textStyle = LocalTextStyle.current.copy(
                fontSize = 28.sp,
                textAlign = TextAlign.Center,
                letterSpacing = 8.sp,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.errorMessage != null,
            supportingText = uiState.errorMessage?.let {
                { Text(it, fontSize = 16.sp, color = MaterialTheme.colorScheme.error) }
            },
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (linkCode.length != 6) return@Button
                viewModel.acceptByCode(linkCode)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            enabled = !uiState.isLoading && linkCode.length == 6,
            colors = ButtonDefaults.buttonColors(containerColor = SafeGreen),
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                Text("연결하기", fontSize = 22.sp)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(
            onClick = onSkip,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
        ) {
            Text("나중에 연결하기", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
