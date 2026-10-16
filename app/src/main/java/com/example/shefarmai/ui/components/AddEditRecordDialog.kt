package com.example.shefarmai.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.shefarmai.data.model.FinancialRecord
import com.example.shefarmai.data.model.TransactionType

@Composable
fun AddEditRecordDialog(
    initialRecord: FinancialRecord?,
    onDismiss: () -> Unit,
    onSave: (
        id: Long,
        type: TransactionType,
        category: String,
        amount: Double?,
        description: String
    ) -> Boolean
) {
    var type by remember { mutableStateOf(initialRecord?.type ?: TransactionType.EXPENSE) }
    var category by remember { mutableStateOf(initialRecord?.category ?: "") }
    var amountText by remember { mutableStateOf(initialRecord?.amount?.toString() ?: "") }
    var description by remember { mutableStateOf(initialRecord?.description ?: "") }
    var localError by remember { mutableStateOf<String?>(null) }

    val isEdit = initialRecord != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEdit) "Edit Financial Record" else "Record Farm Transaction",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (localError != null) {
                    Text(
                        text = localError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Transaction Type",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = type == TransactionType.REVENUE,
                        onClick = { type = TransactionType.REVENUE },
                        label = { Text("Revenue / Sales (+)") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = type == TransactionType.EXPENSE,
                        onClick = { type = TransactionType.EXPENSE },
                        label = { Text("Expense (-)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category *") },
                    placeholder = {
                        Text(if (type == TransactionType.REVENUE) "e.g. Maize Sales, Dairy Sales" else "e.g. Seeds, Fertilizer, Labor")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount (KSh) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description / Notes") },
                    placeholder = { Text("Details like buyer name, receipt #, or item quantity...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    val success = onSave(
                        initialRecord?.id ?: 0L,
                        type,
                        category,
                        amount,
                        description
                    )
                    if (!success) {
                        localError = "Please enter a valid positive amount and category."
                    }
                }
            ) {
                Text(if (isEdit) "Update" else "Save Record")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
