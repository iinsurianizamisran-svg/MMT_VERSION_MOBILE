package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.RegimenType
import com.example.data.model.TreatmentPhase

@Composable
fun NewPatientDialog(
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        ic: String,
        regNum: String,
        age: Int,
        phone: String,
        emergency: String,
        regimen: RegimenType,
        doseMg: Double,
        phase: TreatmentPhase,
        dbbDays: Int,
        notes: String
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var icNumber by remember { mutableStateOf("") }
    var regNum by remember { mutableStateOf("MTM/2026/") }
    var ageStr by remember { mutableStateOf("32") }
    var phone by remember { mutableStateOf("") }
    var emergency by remember { mutableStateOf("") }
    var regimen by remember { mutableStateOf(RegimenType.DOT) }
    var doseStr by remember { mutableStateOf("50.0") }
    var phase by remember { mutableStateOf(TreatmentPhase.INDUKSI) }
    var dbbDays by remember { mutableIntStateOf(2) }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Pendaftaran Pesakit Metadon Baharu",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Penuh Pesakit") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_patient_name")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = icNumber,
                        onValueChange = { icNumber = it },
                        label = { Text("No Kad Pengenalan") },
                        placeholder = { Text("cth: 900101-10-1234") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("input_patient_ic")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = ageStr,
                        onValueChange = { ageStr = it },
                        label = { Text("Umur") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(0.8f)
                            .testTag("input_patient_age")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = regNum,
                    onValueChange = { regNum = it },
                    label = { Text("No Pendaftaran Metadon (MTM)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_patient_reg_no")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("No Telefon Pesakit") },
                    placeholder = { Text("01X-XXXXXXX") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = emergency,
                    onValueChange = { emergency = it },
                    label = { Text("Maklumat Waris / Kecemasan") },
                    placeholder = { Text("Nama & No Telefon Waris") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Regimen Rawatan Metadon:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = regimen == RegimenType.DOT,
                        onClick = { regimen = RegimenType.DOT },
                        label = { Text("DOT (Harian)") },
                        modifier = Modifier.testTag("chip_regimen_dot")
                    )
                    FilterChip(
                        selected = regimen == RegimenType.DOT_DBB,
                        onClick = { regimen = RegimenType.DOT_DBB },
                        label = { Text("DOT & DBB (Bawa Balik)") },
                        modifier = Modifier.testTag("chip_regimen_dot_dbb")
                    )
                }

                if (regimen == RegimenType.DOT_DBB) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Bilangan Hari Dos Bawa Balik (DBB): $dbbDays Hari",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        (1..6).forEach { days ->
                            FilterChip(
                                selected = dbbDays == days,
                                onClick = { dbbDays = days },
                                label = { Text("${days}d") }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Fasa Rawatan:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = phase == TreatmentPhase.INDUKSI,
                        onClick = { phase = TreatmentPhase.INDUKSI },
                        label = { Text("Induksi") }
                    )
                    FilterChip(
                        selected = phase == TreatmentPhase.PENSTABILAN,
                        onClick = { phase = TreatmentPhase.PENSTABILAN },
                        label = { Text("Penstabilan") }
                    )
                    FilterChip(
                        selected = phase == TreatmentPhase.PENGEKALAN,
                        onClick = { phase = TreatmentPhase.PENGEKALAN },
                        label = { Text("Pengekalan") }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = doseStr,
                    onValueChange = { doseStr = it },
                    label = { Text("Dos Permulaan / Semasa (mg)") },
                    supportingText = {
                        val parsed = doseStr.toDoubleOrNull() ?: 0.0
                        Text("Bersamaan ${String.format("%.1f", parsed / 5.0)} ml larutan (5mg/ml)")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_patient_dose")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Catatan Klinikal / Sejarah") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val age = ageStr.toIntOrNull() ?: 30
                        val dose = doseStr.toDoubleOrNull() ?: 50.0
                        onSave(name, icNumber, regNum, age, phone, emergency, regimen, dose, phase, dbbDays, notes)
                    }
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.testTag("btn_save_new_patient")
            ) {
                Text("Daftar Pesakit")
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
