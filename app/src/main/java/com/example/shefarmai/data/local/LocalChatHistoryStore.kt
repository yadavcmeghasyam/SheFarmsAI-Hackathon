package com.example.shefarmai.data.local

import android.content.Context
import com.example.shefarmai.data.model.AssistantMessage
import com.example.shefarmai.data.repository.ChatHistoryStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class LocalChatHistoryStore(context: Context, storageName: String = "assistant_history") : ChatHistoryStore {
    private val preferences = context.applicationContext.getSharedPreferences(storageName, Context.MODE_PRIVATE)

    override suspend fun load(): List<AssistantMessage> = withContext(Dispatchers.IO) {
        val json = JSONArray(preferences.getString("messages", "[]"))
        List(json.length()) { index ->
            val item = json.getJSONObject(index)
            val actions = item.optJSONArray("actions") ?: JSONArray()
            AssistantMessage(
                id = item.getString("id"),
                isUser = item.getBoolean("isUser"),
                text = item.getString("text"),
                timestampInMillis = item.getLong("timestamp"),
                quickActions = List(actions.length()) { actions.getString(it) }
            )
        }
    }

    override suspend fun save(messages: List<AssistantMessage>) = withContext(Dispatchers.IO) {
        val json = JSONArray()
        messages.forEach { message ->
            json.put(JSONObject().apply {
                put("id", message.id)
                put("isUser", message.isUser)
                put("text", message.text)
                put("timestamp", message.timestampInMillis)
                put("actions", JSONArray(message.quickActions))
            })
        }
        val committed = preferences.edit().putString("messages", json.toString()).commit()
        if (!committed) {
            preferences.edit().putString("messages", json.toString()).apply()
        }
    }
}
