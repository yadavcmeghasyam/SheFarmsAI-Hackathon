package com.example.shefarmai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.shefarmai.data.model.AssistantMessage
import org.json.JSONArray

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey
    val id: String,
    val isUser: Boolean,
    val text: String,
    val timestampInMillis: Long,
    val quickActionsJson: String = "[]"
) {
    fun toDomain(): AssistantMessage {
        val actionsArray = try {
            JSONArray(quickActionsJson)
        } catch (_: Exception) {
            JSONArray()
        }
        val actions = List(actionsArray.length()) { actionsArray.getString(it) }
        return AssistantMessage(
            id = id,
            isUser = isUser,
            text = text,
            timestampInMillis = timestampInMillis,
            quickActions = actions
        )
    }

    companion object {
        fun fromDomain(message: AssistantMessage): ChatMessageEntity {
            return ChatMessageEntity(
                id = message.id,
                isUser = message.isUser,
                text = message.text,
                timestampInMillis = message.timestampInMillis,
                quickActionsJson = JSONArray(message.quickActions).toString()
            )
        }
    }
}
