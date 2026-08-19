package com.educalab.graficosdivertidos.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educalab.graficosdivertidos.data.local.entity.UserProfileEntity
import com.educalab.graficosdivertidos.data.repository.ProfileRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userId: Long,
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    val profile: StateFlow<UserProfileEntity?> = profileRepository.observeProfile(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun updateAliasAndAvatar(alias: String, avatarKey: String) {
        viewModelScope.launch { profileRepository.updateAliasAndAvatar(userId, alias, avatarKey) }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch { profileRepository.setSoundEnabled(userId, enabled) }
    }

    fun setHapticsEnabled(enabled: Boolean) {
        viewModelScope.launch { profileRepository.setHapticsEnabled(userId, enabled) }
    }
}
