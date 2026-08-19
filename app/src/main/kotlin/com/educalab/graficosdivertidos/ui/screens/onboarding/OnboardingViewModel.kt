package com.educalab.graficosdivertidos.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educalab.graficosdivertidos.data.repository.ProfileRepository
import com.educalab.graficosdivertidos.ui.components.AVAILABLE_AVATARS
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val page: Int = 0,
    val alias: String = "",
    val selectedAvatar: String = AVAILABLE_AVATARS.first(),
    val completed: Boolean = false,
)

class OnboardingViewModel(
    private val userId: Long,
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    val pageCount = 4

    fun nextPage() = _uiState.update { it.copy(page = (it.page + 1).coerceAtMost(pageCount - 1)) }
    fun previousPage() = _uiState.update { it.copy(page = (it.page - 1).coerceAtLeast(0)) }
    fun setAlias(alias: String) {
        if (alias.length <= 16) _uiState.update { it.copy(alias = alias) }
    }
    fun selectAvatar(avatar: String) = _uiState.update { it.copy(selectedAvatar = avatar) }

    fun finish() {
        val state = _uiState.value
        viewModelScope.launch {
            profileRepository.updateAliasAndAvatar(
                userId,
                alias = state.alias.ifBlank { "Explorador" },
                avatarKey = state.selectedAvatar,
            )
            profileRepository.completeOnboarding(userId)
            _uiState.update { it.copy(completed = true) }
        }
    }
}
