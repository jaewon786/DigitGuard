package com.digitguard.app.ui.guardian

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitguard.app.data.preferences.UserPreferences
import com.digitguard.app.data.remote.api.DashboardResponse
import com.digitguard.app.data.remote.api.ProtectedUserDto
import com.digitguard.app.data.repository.GuardianRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GuardianDashboardUiState(
    val isLoading: Boolean = true,
    val protectedUsers: List<ProtectedUserDto> = emptyList(),
    val selectedDashboard: DashboardResponse? = null,
    val errorMessage: String? = null,
)

@HiltViewModel
class GuardianDashboardViewModel @Inject constructor(
    private val guardianRepository: GuardianRepository,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GuardianDashboardUiState())
    val uiState: StateFlow<GuardianDashboardUiState> = _uiState.asStateFlow()

    init {
        loadProtectedUsers()
    }

    private fun loadProtectedUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val settings = userPreferences.settings.first()
            val guardianId = settings.userId

            if (guardianId.isEmpty()) {
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }

            guardianRepository.getProtectedUsers(guardianId).fold(
                onSuccess = { users ->
                    _uiState.update { it.copy(isLoading = false, protectedUsers = users) }
                },
                onFailure = {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "피보호자 목록을 불러올 수 없습니다.")
                    }
                }
            )
        }
    }

    fun loadDashboard(userId: String) {
        viewModelScope.launch {
            guardianRepository.getDashboard(userId).fold(
                onSuccess = { dashboard ->
                    _uiState.update { it.copy(selectedDashboard = dashboard) }
                },
                onFailure = {
                    _uiState.update { it.copy(errorMessage = "대시보드를 불러올 수 없습니다.") }
                }
            )
        }
    }

    fun respondInstallRequest(requestId: String, decision: String) {
        viewModelScope.launch {
            guardianRepository.respondInstallRequest(requestId, decision)
            // 대시보드 새로고침
            _uiState.value.selectedDashboard?.userId?.let { loadDashboard(it) }
        }
    }

    fun clearDashboard() {
        _uiState.update { it.copy(selectedDashboard = null) }
    }

    fun refresh() {
        loadProtectedUsers()
    }
}
