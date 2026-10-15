package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.DispensationRecord
import com.example.data.model.DispensationStatus
import com.example.data.model.Patient
import com.example.data.model.PatientStatus
import com.example.data.model.RegimenType
import com.example.ui.theme.ClinicalAmberContainer
import com.example.ui.theme.ClinicalAmberTertiary
import com.example.ui.theme.ClinicalCriticalContainer
import com.example.ui.theme.ClinicalCriticalOnContainer
import com.example.ui.theme.ClinicalCriticalRed
import com.example.ui.theme.ClinicalSuccessContainer
import com.example.ui.theme.ClinicalSuccessGreen
import com.example.ui.theme.ClinicalSuccessOnContainer
import com.example.ui.theme.ClinicalTealContainer
import com.example.ui.theme.ClinicalTealOnContainer
import com.example.ui.theme.ClinicalTealPrimary
import com.example.ui.viewmodel.MethadoneUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DispensationScreen(
    state: MethadoneUiState,
    onDispenseClick: (Patient) -> Unit,
    onQuickDotTaken: (Patient) -> Unit,
    onMarkMissedDose: (Patient) -> Unit,
    onPatientClick: (Patient) -> Unit,
    modifier: Modifier = Modifier
) {
    var subTab by remember { mutableIntStateOf(0) }
    var query by remember { mutableStateOf("") }

    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val todayStr = sdf.format(Date())

    val activePatients = state.patients.filter { it.status == PatientStatus.AKTIF }
    val filteredPatients = activePatients.filter {
        query.isBlank() ||
                it.fullName.contains(query, ignoreCase = true) ||
                it.regNumber.contains(query, ignoreCase = true) ||
                it.icNumber.contains(query, ignoreCase = true)
    }

    val todayDispensations = state.dispensations.filter { it.date == todayStr }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Search Bar
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Cari pesakit (Nama, No MTM, No K/P)...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Cari") },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_dispense_patients")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Tab Row: Giliran Hari Ini vs Sejarah Lengkap
        TabRow(
            selectedTabIndex = subTab,
            containerColor = Color.Transparent,
            contentColor = ClinicalTealPrimary
        ) {
            Tab(
                selected = subTab == 0,
                onClick = { subTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Medication, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Giliran Hari Ini (${activePatients.size})", fontWeight = FontWeight.Bold)
                    }
                }
            )
            Tab(
                selected = subTab == 1,
                onClick = { subTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Rekod Dispensi (${state.dispensations.size})", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (subTab == 0) {
            // Today's Queue List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredPatients) { patient ->
                    val todayRecord = todayDispensations.find { it.patientId == patient.id }
                    val criticalAlert = state.alerts.find {
                        it.patientId == patient.id && it.severity == com.example.data.model.AlertSeverity.KRITIKAL
                    }

                    TodayQueueItemCard(
                        patient = patient,
                        todayRecord = todayRecord,
                        criticalAlert = criticalAlert,
                        onDispenseClick = { onDispenseClick(patient) },
                        onQuickDot = { onQuickDotTaken(patient) },
                        onMarkMissed = { onMarkMissedDose(patient) },
                        onCardClick = { onPatientClick(patient) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        } else {
            // Full Dispensation History
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                val sortedRecords = state.dispensations.sortedByDescending { it.timestamp }
                items(sortedRecords) { disp ->
                    HistoryDispensationItem(disp = disp)
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun TodayQueueItemCard(
    patient: Patient,
    todayRecord: DispensationRecord?,
    criticalAlert: com.example.data.model.ClinicalAlert?,
    onDispenseClick: () -> Unit,
    onQuickDot: () -> Unit,
    onMarkMissed: () -> Unit,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("queue_patient_${patient.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (criticalAlert != null) ClinicalCriticalContainer.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = patient.fullName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${patient.regNumber} | No K/P: ${patient.icNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Dosage Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(ClinicalTealContainer)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${patient.currentDoseMg} mg",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = ClinicalTealOnContainer
                        )
                        Text(
                            text = "${String.format("%.1f", patient.doseInMl)} ml",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = ClinicalTealOnContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Regimen & Status tags
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (patient.regimen == RegimenType.DOT_DBB) ClinicalAmberContainer else ClinicalTealContainer)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (patient.regimen == RegimenType.DOT_DBB) "DOT & DBB (${patient.dbbDaysAllowed}h)" else "DOT Sahaja",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = if (patient.regimen == RegimenType.DOT_DBB) ClinicalAmberTertiary else ClinicalTealPrimary
                    )
                }

                Text(
                    text = "\u2022 ${patient.phase.label}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Critical Opioid Tolerance Warning Box
            if (criticalAlert != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ClinicalCriticalRed.copy(alpha = 0.15f))
                        .border(1.dp, ClinicalCriticalRed, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CrisisAlert,
                            contentDescription = null,
                            tint = ClinicalCriticalRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AMARAN: Missed \u2265 3 hari! Toleransi opioid susut. Dilarang dispens tanpa semakan doktor.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = ClinicalCriticalRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action / Status Area
            if (todayRecord != null) {
                // Already recorded today
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            when (todayRecord.status) {
                                DispensationStatus.HADIR_DOT, DispensationStatus.HADIR_DOT_DBB -> ClinicalSuccessContainer
                                DispensationStatus.TIDAK_HADIR_MISSED -> ClinicalCriticalContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (todayRecord.status == DispensationStatus.TIDAK_HADIR_MISSED)
                                    Icons.Default.Close else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (todayRecord.status == DispensationStatus.TIDAK_HADIR_MISSED)
                                    ClinicalCriticalRed else ClinicalSuccessGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = todayRecord.status.label,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (todayRecord.status == DispensationStatus.TIDAK_HADIR_MISSED)
                                        ClinicalCriticalOnContainer else ClinicalSuccessOnContainer
                                )
                                Text(
                                    text = "Pegawai: ${todayRecord.dispenserName}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = if (todayRecord.status == DispensationStatus.TIDAK_HADIR_MISSED)
                                        ClinicalCriticalOnContainer.copy(alpha = 0.8f) else ClinicalSuccessOnContainer.copy(alpha = 0.8f)
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = onDispenseClick,
                            modifier = Modifier.testTag("btn_edit_dispense_${patient.id}")
                        ) {
                            Text("Ubah", fontSize = 12.sp)
                        }
                    }
                }
            } else {
                // Pending Dispensation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onQuickDot,
                        colors = ButtonDefaults.buttonColors(containerColor = ClinicalTealPrimary),
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("btn_quick_dot_${patient.id}")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sahkan Minum (DOT)", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = onDispenseClick,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_full_dispense_${patient.id}")
                    ) {
                        Text(if (patient.regimen == RegimenType.DOT_DBB) "Beri DBB" else "Perincian", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = onMarkMissed,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ClinicalCriticalRed),
                        modifier = Modifier
                            .weight(0.9f)
                            .testTag("btn_missed_${patient.id}")
                    ) {
                        Text("Missed", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryDispensationItem(disp: DispensationRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = disp.patientName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "${disp.date} \u2022 ${disp.status.label}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = when (disp.status) {
                        DispensationStatus.HADIR_DOT, DispensationStatus.HADIR_DOT_DBB -> ClinicalSuccessGreen
                        DispensationStatus.TIDAK_HADIR_MISSED -> ClinicalCriticalRed
                        else -> ClinicalAmberTertiary
                    }
                )
                if (disp.staffNotes.isNotBlank()) {
                    Text(
                        text = "Catatan: ${disp.staffNotes}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                if (disp.dotDoseMg > 0) {
                    Text(
                        text = "${disp.dotDoseMg} mg",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = ClinicalTealPrimary
                    )
                    if (disp.dbbDaysGiven > 0) {
                        Text(
                            text = "+${disp.dbbDaysGiven} Botol DBB",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = ClinicalTealPrimary
                        )
                    }
                } else {
                    Text(
                        text = "0 mg",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = ClinicalCriticalRed
                    )
                }
                Text(
                    text = disp.dispenserName,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
