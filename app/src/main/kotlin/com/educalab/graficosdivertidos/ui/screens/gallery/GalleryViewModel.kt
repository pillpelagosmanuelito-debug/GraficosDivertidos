package com.educalab.graficosdivertidos.ui.screens.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educalab.graficosdivertidos.data.repository.ProfileRepository
import com.educalab.graficosdivertidos.data.repository.ProgressRepository
import com.educalab.graficosdivertidos.domain.model.BadgeModel
import com.educalab.graficosdivertidos.domain.model.ModuleSummary
import com.educalab.graficosdivertidos.domain.model.UserStatsModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class GalleryUiState(
    val stats: UserStatsModel = UserStatsModel(0, 1, 0f, 0, 0, 0, 0),
    val modules: List<ModuleSummary> = emptyList(),
    val badges: List<BadgeModel> = emptyList(),
)

class GalleryViewModel(
    userId: Long,
    profileRepository: ProfileRepository,
    progressRepository: ProgressRepository,
) : ViewModel() {

    val uiState: StateFlow<GalleryUiState> = combine(
        profileRepository.observeStats(userId),
        progressRepository.observeModuleSummaries(userId),
        progressRepository.observeBadges(userId),
    ) { stats, modules, badges -> GalleryUiState(stats, modules, badges) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GalleryUiState())
}
