package com.example.shefarmai.ui.screens.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shefarmai.data.model.AssistantMessage
import com.example.shefarmai.data.repository.AssistantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AssistantUiState(
    val messages: List<AssistantMessage> = emptyList(),
    val inputText: String = "",
    val isGenerating: Boolean = false
)

class AssistantViewModel(
    private val repository: AssistantRepository
) : ViewModel() {

    private val _inputText = MutableStateFlow("")
    private val _isGenerating = MutableStateFlow(false)

    val uiState: StateFlow<AssistantUiState> = combine(
        repository.messages,
        _inputText,
        _isGenerating
    ) { messages, text, generating ->
        AssistantUiState(
            messages = messages,
            inputText = text,
            isGenerating = generating
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AssistantUiState()
    )

    fun onInputTextChanged(text: String) {
        _inputText.value = text
    }

    fun sendMessage(textOverride: String? = null) {
        val queryToSend = textOverride ?: _inputText.value
        if (queryToSend.isBlank()) return

        _inputText.value = ""
        _isGenerating.value = true

        viewModelScope.launch {
            repository.sendMessage(queryToSend)
            _isGenerating.value = false
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    class Factory(
        private val repository: AssistantRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AssistantViewModel(repository) as T
        }
    }
}
