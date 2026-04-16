package com.digitguard.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitguard.app.data.preferences.UserPreferences
import com.digitguard.app.data.repository.ThreatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ThreatInfo(
    val description: String,
    val level: String,
    val time: String,
)

data class HomeUiState(
    val isLoading: Boolean = true,
    val isSafe: Boolean = true,
    val guardianName: String? = null,
    val todayBlockedCount: Int = 0,
    val totalBlockedCount: Int = 0,
    val recentThreats: List<ThreatInfo> = emptyList(),
    val errorMessage: String? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val threatRepository: ThreatRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadUserSettings()
        loadThreatHistory()
    }

    private fun loadUserSettings() {
        viewModelScope.launch {
            userPreferences.settings.collect { settings ->
                _uiState.update {
                    it.copy(guardianName = settings.userName.ifEmpty { null })
                }
            }
        }
    }

    private fun loadThreatHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // 로컬 패턴 수로 차단 통계 추정
                val patterns = threatRepository.getLocalPatternCount()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSafe = true,
                        todayBlockedCount = 0,
                        totalBlockedCount = patterns,
                        recentThreats = emptyList(),
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "데이터를 불러올 수 없습니다.",
                    )
                }
            }
        }
    }

    fun reportThreatDetected(description: String, level: String) {
        _uiState.update { state ->
            val newThreat = ThreatInfo(description, level, "방금 전")
            state.copy(
                isSafe = false,
                todayBlockedCount = state.todayBlockedCount + 1,
                totalBlockedCount = state.totalBlockedCount + 1,
                recentThreats = listOf(newThreat) + state.recentThreats.take(9),
            )
        }
    }

    fun refresh() {
        loadThreatHistory()
    }
}
