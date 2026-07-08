package com.multica.app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multica.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val serverUrl: String = "",
    val useApiToken: Boolean = true,
    val email: String = "",
    val password: String = "",
    val token: String = "",
    val workspaceId: String = "",
    val isLoggedIn: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState(
        serverUrl = authRepository.getServerUrl() ?: "https://aikf.gzkdtc.com"
    ))
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    val isLoggedIn: StateFlow<Boolean> = _uiState
        .map { it.isLoggedIn }
        .stateIn(viewModelScope, SharingStarted.Eagerly, authRepository.isLoggedIn())

    fun updateServerUrl(url: String) {
        _uiState.value = _uiState.value.copy(serverUrl = url)
    }

    fun updateUseApiToken(use: Boolean) {
        _uiState.value = _uiState.value.copy(useApiToken = use)
    }

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun updateToken(token: String) {
        _uiState.value = _uiState.value.copy(token = token)
    }

    fun updateWorkspaceId(id: String) {
        _uiState.value = _uiState.value.copy(workspaceId = id)
    }

    fun login() {
        val state = _uiState.value
        if (state.serverUrl.isBlank()) {
            _uiState.value = state.copy(error = "Please enter server URL")
            return
        }

        authRepository.setServerUrl(state.serverUrl)

        if (state.useApiToken) {
            if (state.token.isBlank()) {
                _uiState.value = state.copy(error = "Please enter API Token")
                return
            }
            authRepository.loginWithToken(state.token, state.workspaceId)
            _uiState.value = state.copy(isLoggedIn = true, error = null)
        } else {
            if (state.email.isBlank() || state.password.isBlank()) {
                _uiState.value = state.copy(error = "Please enter email and password")
                return
            }
            viewModelScope.launch {
                _uiState.value = state.copy(isLoading = true, error = null)
                val result = authRepository.login(state.email, state.password)
                result.fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(isLoading = false, isLoggedIn = true)
                    },
                    onFailure = {
                        _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
                    }
                )
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _uiState.value = LoginUiState(serverUrl = authRepository.getServerUrl() ?: "")
    }
}
