package com.example.shefarmai.ui.screens.finance

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.shefarmai.data.model.FinancialRecord
import com.example.shefarmai.ui.components.AddEditRecordDialog
import com.example.shefarmai.ui.components.FinancialRecordItem
import com.example.shefarmai.ui.components.FinancialSummaryCard

@Composable
fun FinanceScreen(
    viewModel: FinanceViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingRecord by remember { mutableStateOf<FinancialRecord?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingRecord = null
                    showDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Financial Record")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Summary P&L Card
            FinancialSummaryCard(summary = uiState.summary)

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.selectedFilter == "ALL",
                    onClick = { viewModel.onFilterSelected("ALL") },
                    label = { Text("All Records") }
                )
                FilterChip(
                    selected = uiState.selectedFilter == "REVENUE",
                    onClick = { viewModel.onFilterSelected("REVENUE") },
                    label = { Text("Sales (+)") }
                )
                FilterChip(
                    selected = uiState.selectedFilter == "EXPENSE",
                    onClick = { viewModel.onFilterSelected("EXPENSE") },
                    label = { Text("Expenses (-)") }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Transaction History (${uiState.records.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.records.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = "No financial transactions recorded yet. Click the + button below to log your expenses or sales!",
                        modifier = Modifier.padding(24.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.records, key = { it.id }) { record ->
                        FinancialRecordItem(
                            record = record,
                            onEditClick = {
                                editingRecord = record
                                showDialog = true
                            },
                            onDeleteClick = {
                                viewModel.deleteRecord(record)
                            }
                        )
                    }
                }
            }
        }

        if (showDialog) {
            AddEditRecordDialog(
                isSaving = uiState.isSaving,
                initialRecord = editingRecord,
                onDismiss = { showDialog = false },
                onSave = { id, type, category, amount, description ->
                    val success = viewModel.saveRecord(
                        id = id,
                        type = type,
                        category = category,
                        amount = amount,
                        description = description,
                        onSaved = { showDialog = false }
                    )
                    success
                }
            )
        }
    }
}
