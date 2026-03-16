package com.azhua.app.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel() : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun onAction(action: MainAction) {
        viewModelScope.launch {
            when (action) {
                is MainAction.LoadData -> loadData()
            }
        }
    }

    private suspend fun loadData() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        // TODO: implement
        _uiState.value = _uiState.value.copy(isLoading = false)
    }
}

data class MainUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class MainAction {
    object LoadData : MainAction()
}
