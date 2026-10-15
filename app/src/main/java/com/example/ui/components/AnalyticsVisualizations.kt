package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DispensingTrendPoint
import com.example.data.model.DoctorDashboardAnalytics
import com.example.ui.theme.ClinicalAmberTertiary
import com.example.ui.theme.ClinicalCriticalRed
import com.example.ui.theme.ClinicalSuccessGreen
import com.example.ui.theme.ClinicalTealContainer
import com.example.ui.theme.ClinicalTealOnContainer
import com.example.ui.theme.ClinicalTealPrimary
import com.example.ui.theme.MedicalCyanSecondary

@Composable
fun DispensingVolumeChart(
    trendPoints: List<DispensingTrendPoint>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Trend Dispensi Metadon Harian",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Pecahan dos diminum di klinik (DOT) vs dos bawa balik (DBB)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LegendItem(color = ClinicalTealPrimary, label = "DOT (mg)")
                    LegendItem(color = ClinicalAmberTertiary, label = "DBB (mg)")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (trendPoints.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tiada data dispensi", style = MaterialTheme.typography.bodySmall)
                }
            } else {
                val maxVol = (trendPoints.maxOfOrNull { it.totalMg } ?: 300f).coerceAtLeast(100f) * 1.15f

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                ) {
                    val w = size.width
                    val h = size.height
                    val bottomPadding = 30f
                    val chartHeight = h - bottomPadding
                    val barCount = trendPoints.size
                    val barWidth = (w / barCount) * 0.45f
                    val stepX = w / barCount

                    trendPoints.forEachIndexed { idx, point ->
                        val centerX = idx * stepX + stepX / 2f
                        val leftX = centerX - barWidth / 2f

                        val dotH = (point.dotMg / maxVol) * chartHeight
                        val dbbH = (point.dbbMg / maxVol) * chartHeight

                        // Draw DOT bar (base)
                        val dotTopY = chartHeight - dotH
                        drawRoundRect(
                            color = ClinicalTealPrimary,
                            topLeft = Offset(leftX, dotTopY),
                            size = Size(barWidth, dotH),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                        )

                        // Draw DBB bar (stacked above DOT)
                        if (dbbH > 0) {
                            val dbbTopY = dotTopY - dbbH
                            drawRoundRect(
                                color = ClinicalAmberTertiary,
                                topLeft = Offset(leftX, dbbTopY),
                                size = Size(barWidth, dbbH),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                            )
                        }
                    }
                }

                // X-Axis labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    trendPoints.forEach { point ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = point.label,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${point.totalMg.toInt()}mg",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = ClinicalTealPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoShowRatesCard(
    analytics: DoctorDashboardAnalytics,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Kadar Ketidakhadiran Janji Temu (No-Show)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Sasaran pengurangan no-show klinikal < 15%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (analytics.noShowRate < 15f) ClinicalSuccessGreen.copy(alpha = 0.15f) else ClinicalCriticalRed.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${String.format("%.1f", analytics.noShowRate)}% No-Show",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (analytics.noShowRate < 15f) ClinicalSuccessGreen else ClinicalCriticalRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Breakdown stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppointmentStatBadge(
                    label = "Hadir Selesai",
                    count = analytics.attendedAppointmentsCount,
                    color = ClinicalSuccessGreen,
                    modifier = Modifier.weight(1f)
                )
                AppointmentStatBadge(
                    label = "Tertunggak/Batal",
                    count = analytics.noShowAppointmentsCount,
                    color = ClinicalCriticalRed,
                    modifier = Modifier.weight(1f)
                )
                AppointmentStatBadge(
                    label = "Akan Datang",
                    count = analytics.upcomingAppointmentsCount,
                    color = ClinicalTealPrimary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Trend over months
            Text(
                text = "Trend Pengurangan No-Show (Bulanan):",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                analytics.noShowTrend.forEach { (month, rate) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = month,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${String.format("%.1f", rate)}%",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (rate <= 15f) ClinicalSuccessGreen else ClinicalAmberTertiary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TreatmentEffectivenessCard(
    analytics: DoctorDashboardAnalytics,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Indeks Keberkesanan Rawatan",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Skor komposit kepatuhan, ujian air kencing bersih & pengekalan",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(ClinicalTealContainer)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${analytics.treatmentEffectivenessIndex.toInt()} / 100",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = ClinicalTealOnContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Metric 1: Retention Rate
            EffectivenessMetricRow(
                title = "Kadar Pengekalan Rawatan (Retention)",
                value = "${analytics.retentionRate.toInt()}%",
                target = "Sasaran KKM \u2265 80%",
                ratio = analytics.retentionRate / 100f,
                accentColor = ClinicalTealPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Metric 2: Clean UDS Rate
            EffectivenessMetricRow(
                title = "Kadar Saringan Urin Negatif (Bebas Dadah)",
                value = "${analytics.cleanUdsRate.toInt()}%",
                target = "Sasaran KKM \u2265 75%",
                ratio = analytics.cleanUdsRate / 100f,
                accentColor = ClinicalSuccessGreen
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Metric 3: Dose Stabilization Rate
            EffectivenessMetricRow(
                title = "Kestabilan Fasa Pengekalan",
                value = "${analytics.stabilizedDoseRate.toInt()}%",
                target = "Pesakit stabil tanpa eskalasi dos",
                ratio = analytics.stabilizedDoseRate / 100f,
                accentColor = MedicalCyanSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // UDS Progression Trend
            Text(
                text = "Kemajuan UDS Bersih Sepanjang Tempoh Rawatan:",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                analytics.udsTrend.forEach { (label, rate) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${rate.toInt()}% Bebas",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = ClinicalSuccessGreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EffectivenessMetricRow(
    title: String,
    value: String,
    target: String,
    ratio: Float,
    accentColor: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = accentColor
            )
        }
        Text(
            text = target,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { ratio.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = accentColor,
            trackColor = accentColor.copy(alpha = 0.15f)
        )
    }
}

@Composable
private fun AppointmentStatBadge(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(vertical = 8.dp, horizontal = 10.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "$count",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = color
            )
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
