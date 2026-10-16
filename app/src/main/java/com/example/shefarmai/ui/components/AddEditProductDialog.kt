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
import com.example.shefarmai.data.model.ProductListing

@Composable
fun AddEditProductDialog(
    initialListing: ProductListing?,
    onDismiss: () -> Unit,
    onSave: (
        id: Long,
        title: String,
        category: String,
        price: Double?,
        unit: String,
        quantity: Double?,
        location: String,
        contact: String,
        description: String
    ) -> Boolean
) {
    var title by remember { mutableStateOf(initialListing?.title ?: "") }
    var category by remember { mutableStateOf(initialListing?.category ?: "Grains") }
    var priceText by remember { mutableStateOf(initialListing?.pricePerUnit?.toString() ?: "") }
    var unit by remember { mutableStateOf(initialListing?.unit ?: "50kg bag") }
    var quantityText by remember { mutableStateOf(initialListing?.availableQuantity?.toString() ?: "") }
    var location by remember { mutableStateOf(initialListing?.location ?: "") }
    var contact by remember { mutableStateOf(initialListing?.contactContactInfo ?: "") }
    var description by remember { mutableStateOf(initialListing?.description ?: "") }
    var localError by remember { mutableStateOf<String?>(null) }

    val isEdit = initialListing != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEdit) "Edit Marketplace Listing" else "Create Product Listing",
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

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Crop / Produce Title *") },
                    placeholder = { Text("e.g. Organic White Maize") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        placeholder = { Text("e.g. Grains, Vegetables") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unit") },
                        placeholder = { Text("e.g. kg, bag, Liter") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("Price per Unit (KSh) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it },
                        label = { Text("Quantity *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location / County *") },
                    placeholder = { Text("e.g. Eldoret, Uasin Gishu") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = contact,
                    onValueChange = { contact = it },
                    label = { Text("Contact Info / Phone *") },
                    placeholder = { Text("e.g. +254 712 345 678") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    placeholder = { Text("Quality, harvest date, wholesale minimums...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceText.toDoubleOrNull()
                    val quantity = quantityText.toDoubleOrNull()
                    val success = onSave(
                        initialListing?.id ?: 0L,
                        title,
                        category,
                        price,
                        unit,
                        quantity,
                        location,
                        contact,
                        description
                    )
                    if (!success) {
                        localError = "Please check all required fields and enter positive numbers for price and quantity."
                    }
                }
            ) {
                Text(if (isEdit) "Update" else "Post Listing")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
