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
import kotlinx.coroutines.CancellationException

data class AssistantUiState(
    val messages: List<AssistantMessage> = emptyList(),
    val inputText: String = "",
    val isGenerating: Boolean = false,
    val errorMessage: String? = null
)

class AssistantViewModel(
    private val repository: AssistantRepository
) : ViewModel() {

    private val _inputText = MutableStateFlow("")
    private val _isGenerating = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            try {
                repository.loadHistory()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _errorMessage.value = "Could not load saved chat. Try again or clear chat to reset it."
            }
        }
    }

    val uiState: StateFlow<AssistantUiState> = combine(
        repository.messages,
        _inputText,
        _isGenerating,
        _errorMessage
    ) { messages, text, generating, error ->
        AssistantUiState(
            messages = messages,
            inputText = text,
            isGenerating = generating,
            errorMessage = error
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
        if (queryToSend.isBlank() || _isGenerating.value) return

        _inputText.value = ""
        _isGenerating.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                repository.sendMessage(queryToSend)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _inputText.value = queryToSend
                _errorMessage.value = "Could not save your message. Please try again."
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun clearChat() {
        if (_isGenerating.value) return
        _isGenerating.value = true
        viewModelScope.launch {
            try {
                repository.clearHistory()
                _inputText.value = ""
                _errorMessage.value = null
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _errorMessage.value = "Could not clear chat. Please try again."
            } finally {
                _isGenerating.value = false
            }
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
