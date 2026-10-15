package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.example.data.model.DispensationStatus
import com.example.data.model.Patient
import com.example.data.model.RegimenType
import com.example.ui.theme.ClinicalCriticalContainer
import com.example.ui.theme.ClinicalCriticalOnContainer
import com.example.ui.theme.ClinicalCriticalRed
import com.example.ui.theme.ClinicalSuccessContainer
import com.example.ui.theme.ClinicalSuccessOnContainer
import com.example.ui.theme.ClinicalTealPrimary

@Composable
fun DispenseDialog(
    patient: Patient,
    isToleranceLossAlert: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (
        status: DispensationStatus,
        dotDoseMg: Double,
        dbbDoseMg: Double,
        dbbDays: Int,
        dispenserName: String,
        notes: String
    ) -> Unit
) {
    var selectedStatus by remember {
        mutableStateOf(
            if (patient.regimen == RegimenType.DOT_DBB && patient.dbbDaysAllowed > 0)
                DispensationStatus.HADIR_DOT_DBB
            else
                DispensationStatus.HADIR_DOT
        )
    }
    var dispenserName by remember { mutableStateOf("Pegawai Farmasi") }
    var dbbDaysGiven by remember { mutableIntStateOf(patient.dbbDaysAllowed) }
    var notes by remember { mutableStateOf("") }
    var doctorOverrideConfirmed by remember { mutableStateOf(false) }

    val canDispense = !isToleranceLossAlert || doctorOverrideConfirmed || selectedStatus == DispensationStatus.TIDAK_HADIR_MISSED

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Medication,
                    contentDescription = "Dispensi",
                    tint = ClinicalTealPrimary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Dispensi Dos Metadon",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${patient.fullName} (${patient.regNumber})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Dose Information Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Preskripsi Dos Harian",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${patient.currentDoseMg} mg (${String.format("%.1f", patient.doseInMl)} ml)",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = ClinicalTealPrimary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(ClinicalTealPrimary)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = patient.regimen.label,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }
                }

                // Opioid Tolerance Loss Warning
                if (isToleranceLossAlert) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(ClinicalCriticalContainer)
                            .border(1.5.dp, ClinicalCriticalRed, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CrisisAlert,
                                    contentDescription = "Amaran Toleransi",
                                    tint = ClinicalCriticalRed,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "AMARAN KRITIKAL TOLERANSI!",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = ClinicalCriticalOnContainer
                                )
                            }
                            Text(
                                text = "Pesakit ini tidak hadir \u2265 3 hari berturut-turut. Toleransi opioid badan telah susut. Dilarang beri dos tanpa penilaian Pegawai Perubatan (MO).",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = ClinicalCriticalOnContainer
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = doctorOverrideConfirmed,
                                    onCheckedChange = { doctorOverrideConfirmed = it },
                                    modifier = Modifier.testTag("chk_doctor_override")
                                )
                                Text(
                                    text = "Telah diperiksa & dibenarkan oleh Pegawai Perubatan",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = ClinicalCriticalRed
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Status Kehadiran / Tindakan Dispensi:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = selectedStatus == DispensationStatus.HADIR_DOT,
                        onClick = { selectedStatus = DispensationStatus.HADIR_DOT },
                        label = { Text("Hadir DOT (Minum Habis Depan Staf)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("chip_status_hadir_dot")
                    )

                    if (patient.regimen == RegimenType.DOT_DBB) {
                        FilterChip(
                            selected = selectedStatus == DispensationStatus.HADIR_DOT_DBB,
                            onClick = { selectedStatus = DispensationStatus.HADIR_DOT_DBB },
                            label = { Text("Hadir DOT + Bekal Dos Bawa Balik (DBB)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("chip_status_hadir_dbb")
                        )
                    }

                    FilterChip(
                        selected = selectedStatus == DispensationStatus.TIDAK_HADIR_MISSED,
                        onClick = { selectedStatus = DispensationStatus.TIDAK_HADIR_MISSED },
                        label = { Text("Tanda Tidak Hadir (Missed Dose)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("chip_status_missed")
                    )

                    FilterChip(
                        selected = selectedStatus == DispensationStatus.DITANGGUH_KLINIKAL,
                        onClick = { selectedStatus = DispensationStatus.DITANGGUH_KLINIKAL },
                        label = { Text("Ditangguhkan Atas Sebab Klinikal") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // If DBB selected, configure bottles
                if (selectedStatus == DispensationStatus.HADIR_DOT_DBB) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(ClinicalSuccessContainer)
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "Kuantiti Botol DBB: $dbbDaysGiven Botol (${dbbDaysGiven * patient.currentDoseMg}mg)",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = ClinicalSuccessOnContainer
                            )
                            Text(
                                text = "Setiap botol dilabel tarikh pengambilan & disimpan di tempat selamat.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = ClinicalSuccessOnContainer
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(top = 6.dp)
                            ) {
                                (1..6).forEach { d ->
                                    FilterChip(
                                        selected = dbbDaysGiven == d,
                                        onClick = { dbbDaysGiven = d },
                                        label = { Text("${d}b") }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = dispenserName,
                    onValueChange = { dispenserName = it },
                    label = { Text("Nama Pegawai / Staf Bertugas") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_dispenser_name")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Catatan Pemerhatian / Aduan Pesakit") },
                    placeholder = { Text("cth: Tiada tanda sedasi, minum depan staf.") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val dotMg = if (selectedStatus == DispensationStatus.HADIR_DOT || selectedStatus == DispensationStatus.HADIR_DOT_DBB)
                        patient.currentDoseMg else 0.0
                    val dbbMg = if (selectedStatus == DispensationStatus.HADIR_DOT_DBB)
                        patient.currentDoseMg else 0.0
                    val days = if (selectedStatus == DispensationStatus.HADIR_DOT_DBB) dbbDaysGiven else 0

                    onConfirm(selectedStatus, dotMg, dbbMg, days, dispenserName, notes)
                },
                enabled = canDispense,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedStatus == DispensationStatus.TIDAK_HADIR_MISSED)
                        ClinicalCriticalRed else ClinicalTealPrimary
                ),
                modifier = Modifier.testTag("btn_confirm_dispense")
            ) {
                Text(
                    if (selectedStatus == DispensationStatus.TIDAK_HADIR_MISSED)
                        "Sahkan Missed Dose" else "Sahkan Dispensi"
                )
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Batal")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
