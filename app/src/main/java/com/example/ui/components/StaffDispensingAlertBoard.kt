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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.model.DispensingAlertStatus
import com.example.data.model.RegimenType
import com.example.data.model.StaffDispensingReminder
import com.example.ui.theme.ClinicalAmberContainer
import com.example.ui.theme.ClinicalAmberOnContainer
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

@Composable
fun StaffDispensingAlertBoard(
    reminders: List<StaffDispensingReminder>,
    onSendReminder: (patientName: String, phone: String) -> Unit,
    onDispensePatient: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf<DispensingAlertStatus?>(null) }

    val filteredList = reminders.filter {
        selectedFilter == null || it.status == selectedFilter
    }

    val criticalCount = reminders.count { it.status == DispensingAlertStatus.CRITICAL_TOLERANCE_LOSS }
    val lateCount = reminders.count { it.status == DispensingAlertStatus.LATE_WARNING }
    val missedCount = reminders.count { it.status == DispensingAlertStatus.MISSED_FLAGGED }
    val onTimeCount = reminders.count { it.status == DispensingAlertStatus.ON_TIME }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with Session Window Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(ClinicalTealContainer)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = null,
                            tint = ClinicalTealPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Sistem Amaran Dispensi Staf",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Jadual Dispensi: 07:30 AM - 11:30 AM",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (criticalCount > 0) ClinicalCriticalRed else ClinicalSuccessGreen)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (criticalCount > 0) "$criticalCount Kritikal" else "Sesi Berjalan",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { selectedFilter = null },
                    label = { Text("Semua (${reminders.size})", fontSize = 11.sp) }
                )
                if (lateCount > 0) {
                    FilterChip(
                        selected = selectedFilter == DispensingAlertStatus.LATE_WARNING,
                        onClick = { selectedFilter = DispensingAlertStatus.LATE_WARNING },
                        label = { Text("Lewat ($lateCount)", fontSize = 11.sp, color = ClinicalAmberTertiary) }
                    )
                }
                if (criticalCount > 0) {
                    FilterChip(
                        selected = selectedFilter == DispensingAlertStatus.CRITICAL_TOLERANCE_LOSS,
                        onClick = { selectedFilter = DispensingAlertStatus.CRITICAL_TOLERANCE_LOSS },
                        label = { Text("Toleransi ($criticalCount)", fontSize = 11.sp, color = ClinicalCriticalRed) }
                    )
                }
                FilterChip(
                    selected = selectedFilter == DispensingAlertStatus.ON_TIME,
                    onClick = { selectedFilter = DispensingAlertStatus.ON_TIME },
                    label = { Text("Hadir ($onTimeCount)", fontSize = 11.sp, color = ClinicalSuccessGreen) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Reminders list
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                filteredList.forEach { reminder ->
                    StaffReminderItem(
                        reminder = reminder,
                        onSendReminder = { onSendReminder(reminder.patientName, reminder.phone) },
                        onDispense = { onDispensePatient(reminder.patientId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StaffReminderItem(
    reminder: StaffDispensingReminder,
    onSendReminder: () -> Unit,
    onDispense: () -> Unit
) {
    val theme = when (reminder.status) {
        DispensingAlertStatus.CRITICAL_TOLERANCE_LOSS -> StaffReminderTheme(
            ClinicalCriticalContainer,
            ClinicalCriticalRed,
            ClinicalCriticalOnContainer,
            Icons.Default.CrisisAlert
        )
        DispensingAlertStatus.MISSED_FLAGGED -> StaffReminderTheme(
            ClinicalCriticalContainer.copy(alpha = 0.5f),
            ClinicalCriticalRed.copy(alpha = 0.7f),
            ClinicalCriticalOnContainer,
            Icons.Default.Warning
        )
        DispensingAlertStatus.LATE_WARNING -> StaffReminderTheme(
            ClinicalAmberContainer,
            ClinicalAmberTertiary,
            ClinicalAmberOnContainer,
            Icons.Default.Alarm
        )
        DispensingAlertStatus.ON_TIME -> StaffReminderTheme(
            ClinicalSuccessContainer,
            ClinicalSuccessGreen,
            ClinicalSuccessOnContainer,
            Icons.Default.CheckCircle
        )
    }
    val (bgColor, borderClr, textClr, icon) = theme

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderClr.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(12.dp)
            .testTag("staff_reminder_${reminder.patientId}")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = borderClr,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = reminder.patientName,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = textClr
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(borderClr)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = reminder.status.label.split(" ").first(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${reminder.regNumber} \u2022 Dos: ${reminder.doseMg}mg (${String.format("%.1f", reminder.doseMl)}ml)",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                    color = textClr.copy(alpha = 0.85f)
                )
                Text(
                    text = if (reminder.regimen == RegimenType.DOT_DBB) "DOT & DBB" else "DOT",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = borderClr
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = reminder.reminderNote,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = textClr
            )

            if (reminder.status != DispensingAlertStatus.ON_TIME) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onSendReminder,
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("btn_call_reminder_${reminder.patientId}")
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Hubungi", fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Button(
                        onClick = onDispense,
                        colors = ButtonDefaults.buttonColors(containerColor = borderClr),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("btn_dispense_reminder_${reminder.patientId}")
                    ) {
                        Icon(Icons.Default.Medication, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Dispens", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

private data class StaffReminderTheme(
    val bgColor: Color,
    val borderClr: Color,
    val textClr: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
