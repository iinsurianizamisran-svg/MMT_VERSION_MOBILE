package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
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
import com.example.data.model.DoctorSchedulingConfig
import com.example.ui.theme.ClinicalTealContainer
import com.example.ui.theme.ClinicalTealOnContainer
import com.example.ui.theme.ClinicalTealPrimary

@Composable
fun DoctorSchedulingConfigDialog(
    currentConfig: DoctorSchedulingConfig,
    onDismiss: () -> Unit,
    onSave: (DoctorSchedulingConfig) -> Unit
) {
    var inductionDays by remember { mutableIntStateOf(currentConfig.inductionIntervalDays) }
    var stabilizationDays by remember { mutableIntStateOf(currentConfig.stabilizationIntervalDays) }
    var maintenanceDays by remember { mutableIntStateOf(currentConfig.maintenanceIntervalDays) }
    var udsDays by remember { mutableIntStateOf(currentConfig.udsIntervalDays) }
    var dbbDays by remember { mutableIntStateOf(currentConfig.dbbAuditIntervalDays) }
    var highRiskDays by remember { mutableIntStateOf(currentConfig.highRiskIntervalDays) }
    var autoSuggestEnabled by remember { mutableStateOf(currentConfig.autoSuggestEnabled) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(ClinicalTealContainer)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = ClinicalTealPrimary
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Parameter Temujanji Doktor",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Tetapan selang susulan mengikut fasa klinikal",
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
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Auto-suggest toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Penjana Cadangan Automatik",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Sistem mencadangkan tarikh secara pintar berdasarkan kemajuan pesakit.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = autoSuggestEnabled,
                        onCheckedChange = { autoSuggestEnabled = it },
                        modifier = Modifier.testTag("switch_auto_suggest")
                    )
                }

                // 1. Induction Phase Frequency
                ParameterFrequencySelector(
                    title = "Fasa Induksi (1-14 Hari Awal)",
                    description = "Pesakit baru memulakan rawatan metadon. Semakan dos & kestabilan rapi.",
                    selectedValue = inductionDays,
                    options = listOf(3, 5, 7, 10, 14),
                    unit = "Hari",
                    onSelect = { inductionDays = it }
                )

                // 2. Stabilization Phase Frequency
                ParameterFrequencySelector(
                    title = "Fasa Penstabilan",
                    description = "Penetapan dos sasaran yang melegakan ketagihan tanpa kesan sedasi.",
                    selectedValue = stabilizationDays,
                    options = listOf(7, 10, 14, 21, 28),
                    unit = "Hari",
                    onSelect = { stabilizationDays = it }
                )

                // 3. Maintenance Phase Frequency
                ParameterFrequencySelector(
                    title = "Fasa Pengekalan (Maintenance)",
                    description = "Dos stabil jangka panjang. Pemantauan fungsi sosial & pekerjaan.",
                    selectedValue = maintenanceDays,
                    options = listOf(14, 30, 45, 60, 90),
                    unit = "Hari",
                    onSelect = { maintenanceDays = it }
                )

                // 4. Urine Drug Screen (UDS) Interval
                ParameterFrequencySelector(
                    title = "Saringan Urin (UDS) Berkala",
                    description = "Kekerapan ujian saringan dadah dalam air kencing untuk kepatuhan bebas dadah.",
                    selectedValue = udsDays,
                    options = listOf(14, 21, 30, 45, 60),
                    unit = "Hari",
                    onSelect = { udsDays = it }
                )

                // 5. DBB Audit Interval
                ParameterFrequencySelector(
                    title = "Audit Kelayakan Dos Bawa Balik (DBB)",
                    description = "Penilaian berkala syarat keselamatan simpanan ubat bawa balik di rumah.",
                    selectedValue = dbbDays,
                    options = listOf(30, 45, 60, 90),
                    unit = "Hari",
                    onSelect = { dbbDays = it }
                )

                // 6. High-Risk Patient Evaluation
                ParameterFrequencySelector(
                    title = "Pesakit Berisiko Tinggi (Missed Dose / UDS Positif)",
                    description = "Tindakan intervensi segera bagi pesakit yang terlepas dos atau ujian positif.",
                    selectedValue = highRiskDays,
                    options = listOf(1, 2, 3, 5, 7),
                    unit = "Hari",
                    onSelect = { highRiskDays = it }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        DoctorSchedulingConfig(
                            inductionIntervalDays = inductionDays,
                            stabilizationIntervalDays = stabilizationDays,
                            maintenanceIntervalDays = maintenanceDays,
                            udsIntervalDays = udsDays,
                            dbbAuditIntervalDays = dbbDays,
                            highRiskIntervalDays = highRiskDays,
                            autoSuggestEnabled = autoSuggestEnabled
                        )
                    )
                },
                modifier = Modifier.testTag("btn_save_doctor_config")
            ) {
                Text("Simpan Parameter")
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

@Composable
private fun ParameterFrequencySelector(
    title: String,
    description: String,
    selectedValue: Int,
    options: List<Int>,
    unit: String,
    onSelect: (Int) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            options.forEach { opt ->
                FilterChip(
                    selected = selectedValue == opt,
                    onClick = { onSelect(opt) },
                    label = { Text("$opt $unit", fontSize = 11.sp) }
                )
            }
        }
    }
}
