package com.example.shefarmai.data.repository

import com.example.shefarmai.data.model.AssistantMessage

interface ChatHistoryStore {
    suspend fun load(): List<AssistantMessage>
    suspend fun save(messages: List<AssistantMessage>)
}
