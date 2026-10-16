package com.example.shefarmai.data.model

data class AssistantMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val timestampInMillis: Long = System.currentTimeMillis(),
    val quickActions: List<String> = emptyList()
)
