package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClinicalAlert
import com.example.data.model.Patient
import com.example.data.model.RegimenType
import com.example.ui.components.AdherenceTrendChart
import com.example.ui.components.AlertBannerCard
import com.example.ui.theme.ClinicalAmberTertiary
import com.example.ui.theme.ClinicalCriticalRed
import com.example.ui.theme.ClinicalSuccessGreen
import com.example.ui.theme.ClinicalTealContainer
import com.example.ui.theme.ClinicalTealOnContainer
import com.example.ui.theme.ClinicalTealPrimary
import com.example.ui.viewmodel.MethadoneUiState

@Composable
fun DashboardScreen(
    state: MethadoneUiState,
    onViewPatient: (Patient) -> Unit,
    onAutoScheduleAll: () -> Unit,
    onNavigateToDispense: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Dashboard Header Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = ClinicalTealPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Analitik Prestasi Rawatan",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Sistem Pengurusan Terapi Gantian Metadon (KKM)",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Medication,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onNavigateToDispense,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = ClinicalTealPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_dash_go_dispense")
                        ) {
                            Text("Kaunter Dispensi", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = onAutoScheduleAll,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color.White.copy(alpha = 0.8f))),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_dash_auto_schedule")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Auto Temujanji", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Summary Metric Grid Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Kepatuhan Purata",
                    value = "${state.stats.overallAdherenceRate.toInt()}%",
                    subtitle = if (state.stats.overallAdherenceRate >= 85f) "Mencapai KPI (\u226585%)" else "Di bawah sasaran KKM",
                    icon = Icons.Default.TrendingUp,
                    accentColor = if (state.stats.overallAdherenceRate >= 85f) ClinicalSuccessGreen else ClinicalAmberTertiary,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Pesakit Aktif",
                    value = "${state.stats.totalActivePatients}",
                    subtitle = "${state.stats.dotCount} DOT | ${state.stats.dotDbbCount} DOT&DBB",
                    icon = Icons.Default.Group,
                    accentColor = ClinicalTealPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Kehadiran Hari Ini",
                    value = "${state.stats.todayPresentCount} Hadir",
                    subtitle = "${state.stats.todayMissedCount} Missed | ${state.stats.todayPendingCount} Belum",
                    icon = Icons.Default.Medication,
                    accentColor = if (state.stats.todayMissedCount > 0) ClinicalCriticalRed else ClinicalSuccessGreen,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Pesakit Berisiko",
                    value = "${state.stats.highRiskPatientsCount}",
                    subtitle = if (state.stats.highRiskPatientsCount > 0) "Perlu tindakan segera" else "Semua dalam kawalan",
                    icon = Icons.Default.CrisisAlert,
                    accentColor = if (state.stats.highRiskPatientsCount > 0) ClinicalCriticalRed else ClinicalSuccessGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 7-Day Adherence Trend Chart
        item {
            AdherenceTrendChart(dataPoints = state.stats.dailyTrend)
        }

        // Regimen Distribution & Dosage Breakdown Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Taburan Regimen & Profil Dos",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val totalActive = state.stats.totalActivePatients.coerceAtLeast(1)
                    val dotRatio = state.stats.dotCount.toFloat() / totalActive.toFloat()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "DOT Harian (${state.stats.dotCount} pesakit)",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "DOT & DBB (${state.stats.dotDbbCount} pesakit)",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = ClinicalTealPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { dotRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = ClinicalTealPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Dosage range breakdown
                    Text(
                        text = "Taburan Dos Semasa:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val activePatients = state.patients.filter { it.status == com.example.data.model.PatientStatus.AKTIF }
                    val lowDose = activePatients.count { it.currentDoseMg < 50 }
                    val midDose = activePatients.count { it.currentDoseMg in 50.0..80.0 }
                    val highDose = activePatients.count { it.currentDoseMg > 80 }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DosagePill(label = "<50 mg", count = lowDose, modifier = Modifier.weight(1f))
                        DosagePill(label = "50-80 mg", count = midDose, modifier = Modifier.weight(1f))
                        DosagePill(label = ">80 mg", count = highDose, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Automated Clinical Alerts Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Sistem Amaran Kepatuhan",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (state.alerts.isNotEmpty()) ClinicalCriticalRed else ClinicalSuccessGreen)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${state.alerts.size}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }

                if (state.alerts.isNotEmpty()) {
                    Text(
                        text = "Perlu Tindakan MO",
                        style = MaterialTheme.typography.labelSmall,
                        color = ClinicalCriticalRed
                    )
                }
            }
        }

        if (state.alerts.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "\u2705 Tiada amaran klinikal aktif. Semua pesakit mematuhi jadual rawatan.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ClinicalSuccessGreen
                        )
                    }
                }
            }
        } else {
            items(state.alerts) { alert ->
                AlertBannerCard(
                    alert = alert,
                    onViewPatient = { pId ->
                        state.patients.find { it.id == pId }?.let { onViewPatient(it) }
                    },
                    onActionClick = {
                        state.patients.find { it.id == alert.patientId }?.let { onViewPatient(it) }
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
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
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f))
                        .padding(6.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = accentColor
            )
        }
    }
}

@Composable
private fun DosagePill(
    label: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 8.dp, horizontal = 10.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$count Pesakit",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
