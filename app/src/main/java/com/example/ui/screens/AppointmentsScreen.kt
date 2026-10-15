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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tune
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
import com.example.data.model.AlertSeverity
import com.example.data.model.AppointmentStatus
import com.example.data.model.AppointmentType
import com.example.data.model.FollowUpAppointment
import com.example.data.model.SuggestedAppointment
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

@Composable
fun AppointmentsScreen(
    state: MethadoneUiState,
    onAutoScheduleAll: () -> Unit,
    onOpenDoctorConfig: () -> Unit,
    onApplySuggestion: (SuggestedAppointment) -> Unit,
    onApplyAllSuggestions: () -> Unit,
    onAddNewAppointment: () -> Unit,
    onCompleteAppointment: (FollowUpAppointment) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf<AppointmentStatus?>(AppointmentStatus.AKAN_DATANG) }

    val filteredList = state.appointments.filter { apt ->
        selectedFilter == null || apt.status == selectedFilter
    }

    val overdueCount = state.appointments.count { it.status == AppointmentStatus.TERTUNGGAK }
    val unscheduledSuggestions = state.suggestedAppointments.filter { !it.isAlreadyScheduled }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Doctor Parameters & Auto-Scheduling Control Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ClinicalTealContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = ClinicalTealPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Penjadualan Automatik Pintar",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = ClinicalTealOnContainer
                            )
                        }
                        Text(
                            text = "Menjana automatik cadangan tarikh susulan berdasarkan kemajuan pesakit & fasa rawatan.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ClinicalTealOnContainer.copy(alpha = 0.85f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Current Parameters Summary Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ParameterChip(label = "Induksi: ${state.doctorConfig.inductionIntervalDays}h")
                    ParameterChip(label = "Penstabilan: ${state.doctorConfig.stabilizationIntervalDays}h")
                    ParameterChip(label = "Pengekalan: ${state.doctorConfig.maintenanceIntervalDays}h")
                    ParameterChip(label = "UDS: ${state.doctorConfig.udsIntervalDays}h")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onOpenDoctorConfig,
                        colors = ButtonDefaults.buttonColors(containerColor = ClinicalTealPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("btn_open_doctor_params")
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tetapan Parameter", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onAddNewAppointment,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(0.9f)
                            .testTag("btn_manual_appointment")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Manual", fontSize = 11.5.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Smart Suggested Appointments Section
            if (unscheduledSuggestions.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Cadangan Janji Temu Pintar",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(ClinicalTealPrimary)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${unscheduledSuggestions.size}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        }

                        Button(
                            onClick = onApplyAllSuggestions,
                            colors = ButtonDefaults.buttonColors(containerColor = ClinicalSuccessGreen),
                            modifier = Modifier.testTag("btn_apply_all_suggestions")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Jadualkan Semua", fontSize = 11.sp)
                        }
                    }
                }

                items(unscheduledSuggestions) { suggestion ->
                    SuggestedAppointmentCard(
                        suggestion = suggestion,
                        onApply = { onApplySuggestion(suggestion) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }

            // Existing Appointments Filter Chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == AppointmentStatus.AKAN_DATANG,
                        onClick = { selectedFilter = AppointmentStatus.AKAN_DATANG },
                        label = { Text("Akan Datang", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = selectedFilter == AppointmentStatus.TERTUNGGAK,
                        onClick = { selectedFilter = AppointmentStatus.TERTUNGGAK },
                        label = {
                            Text(
                                if (overdueCount > 0) "Tertunggak ($overdueCount)" else "Tertunggak",
                                fontSize = 11.sp,
                                color = if (overdueCount > 0) ClinicalCriticalRed else Color.Unspecified
                            )
                        }
                    )
                    FilterChip(
                        selected = selectedFilter == AppointmentStatus.SELESAI,
                        onClick = { selectedFilter = AppointmentStatus.SELESAI },
                        label = { Text("Selesai", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = { selectedFilter = null },
                        label = { Text("Semua", fontSize = 11.sp) }
                    )
                }
            }

            if (filteredList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tiada rekod janji temu untuk status ini.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(filteredList) { apt ->
                    AppointmentCardItem(
                        apt = apt,
                        onComplete = { onCompleteAppointment(apt) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SuggestedAppointmentCard(
    suggestion: SuggestedAppointment,
    onApply: () -> Unit
) {
    val borderColor = when (suggestion.priority) {
        AlertSeverity.KRITIKAL -> ClinicalCriticalRed
        AlertSeverity.AMARAN_SEDERHANA -> ClinicalAmberTertiary
        AlertSeverity.PERINGATAN -> ClinicalTealPrimary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("sug_card_${suggestion.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, borderColor.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = suggestion.patientName,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${suggestion.regNumber} \u2022 ${suggestion.appointmentType.label}",
                            style = MaterialTheme.typography.bodySmall,
                            color = borderColor
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(borderColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = suggestion.suggestedDate,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = borderColor
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "\uD83D\uDCA1 Rationale: ${suggestion.rationale}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onApply,
                        colors = ButtonDefaults.buttonColors(containerColor = borderColor),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("btn_apply_suggestion_${suggestion.patientId}")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sahkan & Jadualkan", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ParameterChip(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White.copy(alpha = 0.8f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
            color = ClinicalTealOnContainer
        )
    }
}

@Composable
private fun AppointmentCardItem(
    apt: FollowUpAppointment,
    onComplete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("appointment_card_${apt.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = apt.patientName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "No Pendaftaran: ${apt.regNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (apt.isAutoGenerated) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(ClinicalTealContainer)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = ClinicalTealPrimary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Auto",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = ClinicalTealPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Appointment Type & Schedule
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = apt.appointmentType.label,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when (apt.status) {
                                AppointmentStatus.AKAN_DATANG -> ClinicalTealContainer
                                AppointmentStatus.TERTUNGGAK -> ClinicalCriticalContainer
                                AppointmentStatus.SELESAI -> ClinicalSuccessContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = apt.status.label,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = when (apt.status) {
                            AppointmentStatus.AKAN_DATANG -> ClinicalTealOnContainer
                            AppointmentStatus.TERTUNGGAK -> ClinicalCriticalOnContainer
                            AppointmentStatus.SELESAI -> ClinicalSuccessOnContainer
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${apt.scheduledDate} jam ${apt.scheduledTime}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (apt.doctorNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Arahan: ${apt.doctorNotes}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (apt.status != AppointmentStatus.SELESAI) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onComplete,
                        colors = ButtonDefaults.buttonColors(containerColor = ClinicalSuccessGreen),
                        modifier = Modifier.testTag("btn_complete_apt_${apt.id}")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sahkan Selesai (Hadir)", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
