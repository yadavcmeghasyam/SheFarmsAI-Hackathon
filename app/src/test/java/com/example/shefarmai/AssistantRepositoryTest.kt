package com.example.shefarmai

import com.example.shefarmai.data.repository.AssistantRepositoryImpl
import com.example.shefarmai.ui.screens.assistant.AssistantViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AssistantRepositoryTest {
    @get:Rule val main = MainDispatcherRule()

    @Test fun historySurvivesRepositoryRecreationAndClearPersists() = runTest {
        val store = MemoryChatHistory()
        val first = AssistantRepositoryImpl(store)
        first.sendMessage("How do I sell produce?")
        assertTrue(first.messages.value.last().text.contains("Marketplace"))
        val second = AssistantRepositoryImpl(store)
        second.loadHistory()
        assertEquals(first.messages.value, second.messages.value)
        second.clearHistory()
        val third = AssistantRepositoryImpl(store)
        third.loadHistory()
        assertEquals(1, third.messages.value.size)
        assertTrue(store.saved.isEmpty())
    }

    @Test fun blankQueriesDoNotAddMessagesAndLiveDataIsNotInvented() = runTest {
        val repo = AssistantRepositoryImpl()
        repo.sendMessage("   ")
        assertEquals(1, repo.messages.value.size)
        repo.sendMessage("What is the weather today?")
        assertTrue(repo.messages.value.last().text.contains("cannot access live weather"))
    }

    @Test fun failedSendRestoresInputAndClearsBusyState() = runTest {
        val store = MemoryChatHistory().apply { failWrites = true }
        val repo = AssistantRepositoryImpl(store)
        val vm = AssistantViewModel(repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect() }
        vm.sendMessage("compost")
        vm.sendMessage("duplicate")
        runCurrent()
        assertFalse(vm.uiState.value.isGenerating)
        assertEquals("compost", vm.uiState.value.inputText)
        assertEquals(1, vm.uiState.value.messages.size)
        assertNotNull(vm.uiState.value.errorMessage)
        store.failWrites = false
        vm.sendMessage()
        runCurrent()
        assertEquals(3, vm.uiState.value.messages.size)
        assertNull(vm.uiState.value.errorMessage)
    }
}
