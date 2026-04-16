package com.digitguard.app.ui.guardian

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitguard.app.data.preferences.UserPreferences
import com.digitguard.app.data.repository.GuardianRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GuardianLinkUiState(
    val isLoading: Boolean = false,
    val isLinked: Boolean = false,
    val guardianName: String? = null,
    val errorMessage: String? = null,
)

@HiltViewModel
class GuardianLinkViewModel @Inject constructor(
    private val guardianRepository: GuardianRepository,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GuardianLinkUiState())
    val uiState: StateFlow<GuardianLinkUiState> = _uiState.asStateFlow()

    fun acceptByCode(code: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val userId = "dev-user" // TODO: 실제 사용자 ID

            guardianRepository.acceptByCode(code, userId).fold(
                onSuccess = { response ->
                    userPreferences.setGuardianPhone("") // TODO: 실제 전화번호
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLinked = true,
                            guardianName = response.guardianName,
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "연결에 실패했습니다. 코드를 확인해 주세요.",
                        )
                    }
                }
            )
        }
    }
}
