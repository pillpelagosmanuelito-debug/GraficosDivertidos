package com.educalab.graficosdivertidos.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educalab.graficosdivertidos.data.local.entity.UserProfileEntity
import com.educalab.graficosdivertidos.data.repository.ProfileRepository
import com.educalab.graficosdivertidos.data.repository.ProgressRepository
import com.educalab.graficosdivertidos.domain.model.ModuleSummary
import com.educalab.graficosdivertidos.domain.model.UserStatsModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val profile: UserProfileEntity? = null,
    val stats: UserStatsModel = UserStatsModel(0, 1, 0f, 0, 0, 0, 0),
    val modules: List<ModuleSummary> = emptyList(),
)

class HomeViewModel(
    userId: Long,
    profileRepository: ProfileRepository,
    progressRepository: ProgressRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        profileRepository.observeProfile(userId),
        profileRepository.observeStats(userId),
        progressRepository.observeModuleSummaries(userId),
    ) { profile, stats, modules ->
        HomeUiState(profile, stats, modules)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())
}
