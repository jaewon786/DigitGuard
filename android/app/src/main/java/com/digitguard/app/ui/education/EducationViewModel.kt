package com.digitguard.app.ui.education

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitguard.app.data.remote.api.EducationContentDto
import com.digitguard.app.data.repository.EducationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EducationUiState(
    val isLoading: Boolean = true,
    val contents: List<EducationContentDto> = emptyList(),
    val selectedDetail: EducationContentDto? = null,
    val errorMessage: String? = null,
)

@HiltViewModel
class EducationViewModel @Inject constructor(
    private val repository: EducationRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EducationUiState())
    val uiState: StateFlow<EducationUiState> = _uiState.asStateFlow()

    init {
        loadContents()
    }

    private fun loadContents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getContents().fold(
                onSuccess = { contents ->
                    _uiState.update { it.copy(isLoading = false, contents = contents) }
                },
                onFailure = {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "콘텐츠를 불러올 수 없습니다.") }
                }
            )
        }
    }

    fun selectContent(id: String) {
        viewModelScope.launch {
            repository.getContentById(id).fold(
                onSuccess = { detail ->
                    _uiState.update { it.copy(selectedDetail = detail) }
                },
                onFailure = {
                    _uiState.update { it.copy(errorMessage = "상세 내용을 불러올 수 없습니다.") }
                }
            )
        }
    }

    fun clearDetail() {
        _uiState.update { it.copy(selectedDetail = null) }
    }

    fun refresh() {
        loadContents()
    }
}
