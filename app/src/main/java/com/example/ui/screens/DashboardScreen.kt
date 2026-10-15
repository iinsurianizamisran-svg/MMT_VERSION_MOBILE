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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
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
import com.example.data.model.SuggestedAppointment
import com.example.ui.components.AdherenceTrendChart
import com.example.ui.components.AlertBannerCard
import com.example.ui.components.DispensingVolumeChart
import com.example.ui.components.NoShowRatesCard
import com.example.ui.components.TreatmentEffectivenessCard
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
    onOpenDoctorConfig: () -> Unit,
    onApplySuggestion: (SuggestedAppointment) -> Unit,
    onNavigateToDispense: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    onSelectAnalyticsTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val analytics = state.doctorAnalytics

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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Dashboard Analitik Doktor",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Petunjuk Prestasi Utama (KPI) & Pemantauan Rawatan Metadon",
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
                            onClick = onOpenDoctorConfig,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = ClinicalTealPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("btn_dash_doctor_config")
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tetapan Parameter", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        }

                        OutlinedButton(
                            onClick = onAutoScheduleAll,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(Color.White.copy(alpha = 0.8f))
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_dash_auto_schedule")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Auto-Jadual", fontWeight = FontWeight.SemiBold, fontSize = 11.5.sp)
                        }
                    }
                }
            }
        }

        // Summary Metric Grid Cards (Top 4 KPIs)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Kepatuhan Purata",
                    value = "${analytics.overallAdherenceRate.toInt()}%",
                    subtitle = if (analytics.overallAdherenceRate >= 85f) "Mencapai KPI (\u226585%)" else "Di bawah KPI KKM",
                    icon = Icons.Default.TrendingUp,
                    accentColor = if (analytics.overallAdherenceRate >= 85f) ClinicalSuccessGreen else ClinicalAmberTertiary,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Dispensi Hari Ini",
                    value = "${analytics.todayTotalVolumeMg.toInt()} mg",
                    subtitle = "DOT: ${analytics.todayDotDoseMg.toInt()}mg | DBB: ${analytics.todayDbbDoseMg.toInt()}mg",
                    icon = Icons.Default.Medication,
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
                    title = "Kadar No-Show",
                    value = "${String.format("%.1f", analytics.noShowRate)}%",
                    subtitle = if (analytics.noShowRate <= 15f) "Terkawal (< 15%)" else "Perlu intervensi",
                    icon = Icons.Default.EventBusy,
                    accentColor = if (analytics.noShowRate <= 15f) ClinicalSuccessGreen else ClinicalCriticalRed,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Skor Keberkesanan",
                    value = "${analytics.treatmentEffectivenessIndex.toInt()} / 100",
                    subtitle = "UDS Bersih: ${analytics.cleanUdsRate.toInt()}%",
                    icon = Icons.Default.Science,
                    accentColor = ClinicalSuccessGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 4 KPI Dimension Tab Navigation Bar
        item {
            Column {
                Text(
                    text = "Dimensi Analitik Doktor",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                ScrollableTabRow(
                    selectedTabIndex = state.activeAnalyticsTab,
                    containerColor = Color.Transparent,
                    contentColor = ClinicalTealPrimary,
                    edgePadding = 0.dp
                ) {
                    val tabs = listOf(
                        "Kepatuhan Pesakit",
                        "Trend Dispensi",
                        "Kadar No-Show",
                        "Keberkesanan Rawatan"
                    )
                    tabs.forEachIndexed { index, label ->
                        Tab(
                            selected = state.activeAnalyticsTab == index,
                            onClick = { onSelectAnalyticsTab(index) },
                            text = {
                                Text(
                                    text = label,
                                    fontWeight = if (state.activeAnalyticsTab == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            }
                        )
                    }
                }
            }
        }

        // Render Active Analytics Section
        when (state.activeAnalyticsTab) {
            0 -> {
                // Tab 0: Adherence Dimension
                item {
                    AdherenceTrendChart(dataPoints = analytics.adherenceTrend)
                }

                item {
                    // Regimen Adherence Breakdown Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Perbandingan Kepatuhan Mengikut Regimen",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Regimen DOT Sahaja: ${analytics.dotAdherenceRate.toInt()}%",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = "Regimen DOT & DBB: ${analytics.dotDbbAdherenceRate.toInt()}%",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = ClinicalTealPrimary
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { analytics.dotDbbAdherenceRate / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = ClinicalTealPrimary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }

            1 -> {
                // Tab 1: Medication Dispensing Trends Dimension
                item {
                    DispensingVolumeChart(trendPoints = analytics.dispensingVolumeTrend)
                }

                item {
                    // Today's Dispensing Breakdown Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Ringkasan Jumlah Dos Hari Ini",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(ClinicalTealContainer)
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "DOT Klinik",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = ClinicalTealOnContainer
                                        )
                                        Text(
                                            text = "${analytics.todayDotDoseMg.toInt()} mg",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = ClinicalTealOnContainer
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(ClinicalAmberTertiary.copy(alpha = 0.15f))
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "DBB Bawa Balik",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = ClinicalAmberTertiary
                                        )
                                        Text(
                                            text = "${analytics.todayDbbDoseMg.toInt()} mg",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = ClinicalAmberTertiary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Tab 2: Appointment No-Show Rates Dimension
                item {
                    NoShowRatesCard(analytics = analytics)
                }
            }

            3 -> {
                // Tab 3: Treatment Effectiveness Over Time Dimension
                item {
                    TreatmentEffectivenessCard(analytics = analytics)
                }
            }
        }

        // Automated Appointment Suggestions Section (Doctor Parameter Driven)
        if (state.suggestedAppointments.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = ClinicalTealPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Cadangan Susulan Automatik (${state.suggestedAppointments.size})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    OutlinedButton(
                        onClick = onNavigateToAppointments,
                        modifier = Modifier.testTag("btn_see_all_suggestions")
                    ) {
                        Text("Lihat Semua", fontSize = 11.sp)
                    }
                }
            }

            // Show top 2 suggestions directly on dashboard
            items(state.suggestedAppointments.take(2)) { suggestion ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = ClinicalTealContainer.copy(alpha = 0.4f))
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
                                text = "${suggestion.patientName} (${suggestion.regNumber})",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Cadangan: ${suggestion.suggestedDate} \u2022 ${suggestion.appointmentType.label}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = ClinicalTealPrimary
                            )
                            Text(
                                text = suggestion.rationale,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = { onApplySuggestion(suggestion) },
                            colors = ButtonDefaults.buttonColors(containerColor = ClinicalTealPrimary),
                            modifier = Modifier.testTag("btn_apply_sug_${suggestion.patientId}")
                        ) {
                            Text("Tetapkan", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Active Clinical Alerts Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Sistem Amaran Kepatuhan Klinikal",
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
                        text = "Tindakan MO",
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
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "\u2705 Tiada amaran klinikal aktif. Semua pesakit patuh jadual rawatan.",
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
