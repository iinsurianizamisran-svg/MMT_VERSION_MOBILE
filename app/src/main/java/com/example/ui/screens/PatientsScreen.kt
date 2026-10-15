package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DispensationStatus
import com.example.data.model.Patient
import com.example.data.model.RegimenType
import com.example.ui.theme.ClinicalAmberContainer
import com.example.ui.theme.ClinicalAmberTertiary
import com.example.ui.theme.ClinicalCriticalRed
import com.example.ui.theme.ClinicalSuccessGreen
import com.example.ui.theme.ClinicalTealContainer
import com.example.ui.theme.ClinicalTealOnContainer
import com.example.ui.theme.ClinicalTealPrimary
import com.example.ui.viewmodel.MethadoneUiState

@Composable
fun PatientsScreen(
    state: MethadoneUiState,
    onPatientClick: (Patient) -> Unit,
    onDispenseClick: (Patient) -> Unit,
    onUdsClick: (Patient) -> Unit,
    onAddNewPatient: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<RegimenType?>(null) }

    val filteredList = state.patients.filter { p ->
        val matchesQuery = searchQuery.isBlank() ||
                p.fullName.contains(searchQuery, ignoreCase = true) ||
                p.regNumber.contains(searchQuery, ignoreCase = true) ||
                p.icNumber.contains(searchQuery, ignoreCase = true)

        val matchesFilter = selectedFilter == null || p.regimen == selectedFilter
        matchesQuery && matchesFilter
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddNewPatient,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Daftar Pesakit", fontWeight = FontWeight.Bold) },
                containerColor = ClinicalTealPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_add_patient")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari mengikut Nama, No MTM, No K/P...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Cari") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_patients_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { selectedFilter = null },
                    label = { Text("Semua (${state.patients.size})") }
                )
                FilterChip(
                    selected = selectedFilter == RegimenType.DOT,
                    onClick = { selectedFilter = RegimenType.DOT },
                    label = { Text("DOT Sahaja") }
                )
                FilterChip(
                    selected = selectedFilter == RegimenType.DOT_DBB,
                    onClick = { selectedFilter = RegimenType.DOT_DBB },
                    label = { Text("DOT & DBB") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tiada pesakit ditemui mengikut carian.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredList) { patient ->
                        // Calculate individual patient adherence
                        val patientDisps = state.dispensations.filter { it.patientId == patient.id }
                        val attended = patientDisps.count {
                            it.status == DispensationStatus.HADIR_DOT || it.status == DispensationStatus.HADIR_DOT_DBB
                        }
                        val rate = if (patientDisps.isNotEmpty()) (attended.toFloat() / patientDisps.size.toFloat()) * 100f else 100f

                        PatientDirectoryCard(
                            patient = patient,
                            adherenceRate = rate,
                            onClick = { onPatientClick(patient) },
                            onDispense = { onDispenseClick(patient) },
                            onUds = { onUdsClick(patient) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp)) // Fab padding
                    }
                }
            }
        }
    }
}

@Composable
private fun PatientDirectoryCard(
    patient: Patient,
    adherenceRate: Float,
    onClick: () -> Unit,
    onDispense: () -> Unit,
    onUds: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("patient_card_${patient.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(ClinicalTealContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = ClinicalTealPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = patient.fullName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${patient.regNumber} \u2022 ${patient.age} thn",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Dose Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(ClinicalTealContainer)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${patient.currentDoseMg} mg",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = ClinicalTealOnContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Details info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (patient.regimen == RegimenType.DOT_DBB) ClinicalAmberContainer else ClinicalTealContainer)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (patient.regimen == RegimenType.DOT_DBB) "DOT & DBB (${patient.dbbDaysAllowed}h)" else "DOT",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = if (patient.regimen == RegimenType.DOT_DBB) ClinicalAmberTertiary else ClinicalTealPrimary
                        )
                    }

                    Text(
                        text = patient.phase.label.split(" ").first(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }

                Text(
                    text = "Kepatuhan: ${adherenceRate.toInt()}%",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (adherenceRate >= 85f) ClinicalSuccessGreen else ClinicalCriticalRed
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // UDS status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "UDS: ${patient.lastUdsResult}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                    color = if (patient.lastUdsResult.contains("Positif", ignoreCase = true)) ClinicalCriticalRed else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Tel: ${patient.phone}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onDispense,
                    modifier = Modifier
                        .weight(1.2f)
                        .testTag("btn_dispense_card_${patient.id}")
                ) {
                    Icon(Icons.Default.Medication, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Dispens", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onUds,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_uds_card_${patient.id}")
                ) {
                    Icon(Icons.Default.Science, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("UDS", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onClick,
                    modifier = Modifier
                        .weight(1.1f)
                        .testTag("btn_profile_card_${patient.id}")
                ) {
                    Text("Profil", fontSize = 12.sp)
                }
            }
        }
    }
}
