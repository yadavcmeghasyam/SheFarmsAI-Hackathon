package com.example.shefarmai

import com.example.shefarmai.data.repository.AssistantRepositoryImpl
import com.example.shefarmai.ui.screens.assistant.AssistantViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AssistantViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: AssistantRepositoryImpl
    private lateinit var viewModel: AssistantViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = AssistantRepositoryImpl()
        viewModel = AssistantViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun sendMessage_addsUserAndAssistantResponse() = runTest {
        viewModel.onInputTextChanged("How to treat fall armyworm?")
        viewModel.sendMessage()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(3, state.messages.size) // Welcome + User + Assistant
        assertTrue(state.messages[1].isUser)
        assertEquals("How to treat fall armyworm?", state.messages[1].text)
        assertFalse(state.messages[2].isUser)
        assertTrue(state.messages[2].text.contains("Armyworm"))
    }

    @Test
    fun clearChat_resetsMessagesToWelcome() = runTest {
        viewModel.sendMessage("Test query")
        advanceUntilIdle()

        viewModel.clearChat()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.messages.size)
        assertFalse(state.messages[0].isUser)
    }
}
