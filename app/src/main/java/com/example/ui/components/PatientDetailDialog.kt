package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DispensationRecord
import com.example.data.model.DispensationStatus
import com.example.data.model.Patient
import com.example.data.model.RegimenType
import com.example.ui.theme.ClinicalAmberTertiary
import com.example.ui.theme.ClinicalCriticalRed
import com.example.ui.theme.ClinicalSuccessGreen
import com.example.ui.theme.ClinicalTealContainer
import com.example.ui.theme.ClinicalTealOnContainer
import com.example.ui.theme.ClinicalTealPrimary

@Composable
fun PatientDetailDialog(
    patient: Patient,
    dispensationHistory: List<DispensationRecord>,
    onDismiss: () -> Unit,
    onDispenseClick: () -> Unit,
    onUdsClick: () -> Unit,
    onAppointmentClick: () -> Unit
) {
    val patientHistory = dispensationHistory.filter { it.patientId == patient.id }
    val totalRecorded = patientHistory.size
    val attendedCount = patientHistory.count {
        it.status == DispensationStatus.HADIR_DOT || it.status == DispensationStatus.HADIR_DOT_DBB
    }
    val adherencePercent = if (totalRecorded > 0) {
        (attendedCount.toFloat() / totalRecorded.toFloat()) * 100f
    } else {
        100f
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = patient.fullName,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "No. Pendaftaran: ${patient.regNumber} | No. K/P: ${patient.icNumber}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_patient_detail")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Clinical summary cards row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = ClinicalTealContainer)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Preskripsi Dos",
                                style = MaterialTheme.typography.labelSmall,
                                color = ClinicalTealOnContainer
                            )
                            Text(
                                text = "${patient.currentDoseMg} mg",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = ClinicalTealOnContainer
                            )
                            Text(
                                text = "${String.format("%.1f", patient.doseInMl)} ml sirap",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = ClinicalTealOnContainer.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Regimen",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (patient.regimen == RegimenType.DOT_DBB) "DOT + DBB" else "DOT Sahaja",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (patient.regimen == RegimenType.DOT_DBB) "${patient.dbbDaysAllowed} Hari DBB" else "Minum Klinik",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Kepatuhan",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${adherencePercent.toInt()}%",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (adherencePercent >= 85f) ClinicalSuccessGreen else ClinicalCriticalRed
                            )
                            Text(
                                text = if (adherencePercent >= 85f) "Cemerlang" else "Perlu Pantau",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = if (adherencePercent >= 85f) ClinicalSuccessGreen else ClinicalCriticalRed
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Patient metadata & UDS info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Fasa Rawatan: ${patient.phase.label}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "Mula: ${patient.startDate}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Hubungi: ${patient.phone} | Waris: ${patient.emergencyContact}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Status UDS: ${patient.lastUdsResult} (${patient.lastUdsDate.ifBlank { "Tiada rekod" }})",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = if (patient.lastUdsResult.contains("Positif", ignoreCase = true))
                                    ClinicalCriticalRed else ClinicalTealPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Fast Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onDispenseClick,
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("btn_detail_dispense")
                    ) {
                        Icon(Icons.Default.Medication, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Dispens Dos", fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = onUdsClick,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_detail_uds")
                    ) {
                        Icon(Icons.Default.Science, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("UDS", fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = onAppointmentClick,
                        modifier = Modifier
                            .weight(1.1f)
                            .testTag("btn_detail_appointment")
                    ) {
                        Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Temujanji", fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Divider()
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Sejarah Log Pengambilan & Dispensi (${patientHistory.size} Rekod):",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (patientHistory.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tiada rekod dispensi lampau untuk pesakit ini.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(patientHistory) { disp ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = when (disp.status) {
                                        DispensationStatus.TIDAK_HADIR_MISSED -> ClinicalCriticalRed.copy(alpha = 0.08f)
                                        DispensationStatus.HADIR_DOT, DispensationStatus.HADIR_DOT_DBB -> ClinicalSuccessGreen.copy(alpha = 0.08f)
                                        else -> MaterialTheme.colorScheme.surface
                                    }
                                )
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
                                            text = disp.date,
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = disp.status.label,
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = when (disp.status) {
                                                DispensationStatus.TIDAK_HADIR_MISSED -> ClinicalCriticalRed
                                                DispensationStatus.HADIR_DOT, DispensationStatus.HADIR_DOT_DBB -> ClinicalSuccessGreen
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
                                        if (disp.status == DispensationStatus.HADIR_DOT || disp.status == DispensationStatus.HADIR_DOT_DBB) {
                                            Text(
                                                text = "${disp.dotDoseMg} mg",
                                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                                color = ClinicalTealPrimary
                                            )
                                            if (disp.dbbDaysGiven > 0) {
                                                Text(
                                                    text = "+ ${disp.dbbDaysGiven} Hari DBB",
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
                    }
                }
            }
        }
    }
}
