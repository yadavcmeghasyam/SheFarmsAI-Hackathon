package com.example.shefarmai.data.repository

import com.example.shefarmai.data.model.AssistantMessage
import kotlinx.coroutines.flow.StateFlow

interface AssistantRepository {
    val messages: StateFlow<List<AssistantMessage>>
    suspend fun loadHistory() {}
    suspend fun sendMessage(userText: String)
    suspend fun clearHistory()
}
