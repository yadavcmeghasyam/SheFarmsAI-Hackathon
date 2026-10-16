package com.example.shefarmai.data.local

import com.example.shefarmai.data.local.dao.ChatMessageDao
import com.example.shefarmai.data.local.entity.ChatMessageEntity
import com.example.shefarmai.data.model.AssistantMessage
import com.example.shefarmai.data.repository.ChatHistoryStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomChatHistoryStore(
    private val chatMessageDao: ChatMessageDao,
    private val legacyStore: LocalChatHistoryStore? = null
) : ChatHistoryStore {

    override suspend fun load(): List<AssistantMessage> = withContext(Dispatchers.IO) {
        val entities = chatMessageDao.getAll()
        if (entities.isEmpty() && legacyStore != null) {
            val legacy = legacyStore.load()
            if (legacy.isNotEmpty()) {
                chatMessageDao.insertAll(legacy.map { ChatMessageEntity.fromDomain(it) })
                return@withContext legacy
            }
        }
        entities.map { it.toDomain() }
    }

    override suspend fun save(messages: List<AssistantMessage>) = withContext(Dispatchers.IO) {
        chatMessageDao.deleteAll()
        if (messages.isNotEmpty()) {
            chatMessageDao.insertAll(messages.takeLast(200).map { ChatMessageEntity.fromDomain(it) })
        }
    }
}
