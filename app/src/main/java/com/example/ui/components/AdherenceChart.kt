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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ClinicalAmberTertiary
import com.example.ui.theme.ClinicalSuccessGreen
import com.example.ui.theme.ClinicalTealPrimary

@Composable
fun AdherenceTrendChart(
    dataPoints: List<Pair<String, Float>>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Trend Kepatuhan 7 Hari",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Sasaran KPI KKM: \u2265 85% Kepatuhan",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(ClinicalTealPrimary, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Kepatuhan (%)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (dataPoints.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tiada data rekod kepatuhan yang mencukupi", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    val width = size.width
                    val height = size.height
                    val bottomPadding = 36f
                    val topPadding = 20f
                    val graphHeight = height - bottomPadding - topPadding

                    val minVal = 60f
                    val maxVal = 100f
                    val range = maxVal - minVal

                    // Draw 85% KPI benchmark line (dashed)
                    val kpiY = topPadding + graphHeight * (1f - (85f - minVal) / range)
                    drawLine(
                        color = ClinicalAmberTertiary.copy(alpha = 0.7f),
                        start = Offset(0f, kpiY),
                        end = Offset(width, kpiY),
                        strokeWidth = 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )

                    // Draw Grid lines
                    val stepY = graphHeight / 4
                    for (i in 0..4) {
                        val y = topPadding + i * stepY
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.3f),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1f
                        )
                    }

                    // Compute points
                    val stepX = if (dataPoints.size > 1) width / (dataPoints.size - 1) else width
                    val points = dataPoints.mapIndexed { idx, item ->
                        val clampedVal = item.second.coerceIn(minVal, maxVal)
                        val x = idx * stepX
                        val y = topPadding + graphHeight * (1f - (clampedVal - minVal) / range)
                        Offset(x, y)
                    }

                    // Draw gradient fill area
                    if (points.isNotEmpty()) {
                        val fillPath = Path().apply {
                            moveTo(points.first().x, topPadding + graphHeight)
                            points.forEach { lineTo(it.x, it.y) }
                            lineTo(points.last().x, topPadding + graphHeight)
                            close()
                        }
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    ClinicalTealPrimary.copy(alpha = 0.28f),
                                    ClinicalTealPrimary.copy(alpha = 0.02f)
                                )
                            )
                        )

                        // Draw smooth line
                        val strokePath = Path().apply {
                            moveTo(points.first().x, points.first().y)
                            for (i in 1 until points.size) {
                                val p0 = points[i - 1]
                                val p1 = points[i]
                                val cx = (p0.x + p1.x) / 2f
                                cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                            }
                        }
                        drawPath(
                            path = strokePath,
                            color = ClinicalTealPrimary,
                            style = Stroke(width = 4f)
                        )

                        // Draw point markers
                        points.forEachIndexed { _, p ->
                            drawCircle(
                                color = Color.White,
                                radius = 6f,
                                center = p
                            )
                            drawCircle(
                                color = ClinicalTealPrimary,
                                radius = 4f,
                                center = p
                            )
                        }
                    }
                }

                // X-Axis labels
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    dataPoints.forEach { item ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = item.first,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${item.second.toInt()}%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (item.second >= 85f) ClinicalSuccessGreen else ClinicalAmberTertiary
                            )
                        }
                    }
                }
            }
        }
    }
}
