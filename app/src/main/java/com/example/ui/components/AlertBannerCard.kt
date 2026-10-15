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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.model.AlertSeverity
import com.example.data.model.ClinicalAlert
import com.example.data.model.Patient
import com.example.ui.theme.ClinicalAmberContainer
import com.example.ui.theme.ClinicalAmberOnContainer
import com.example.ui.theme.ClinicalAmberTertiary
import com.example.ui.theme.ClinicalCriticalContainer
import com.example.ui.theme.ClinicalCriticalOnContainer
import com.example.ui.theme.ClinicalCriticalRed
import com.example.ui.theme.MedicalCyanContainer
import com.example.ui.theme.MedicalCyanOnContainer
import com.example.ui.theme.MedicalCyanSecondary

@Composable
fun AlertBannerCard(
    alert: ClinicalAlert,
    onViewPatient: (String) -> Unit,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (containerBg, contentColor, borderColor, icon) = when (alert.severity) {
        AlertSeverity.KRITIKAL -> Quadruple(
            ClinicalCriticalContainer,
            ClinicalCriticalOnContainer,
            ClinicalCriticalRed,
            Icons.Default.CrisisAlert
        )
        AlertSeverity.AMARAN_SEDERHANA -> Quadruple(
            ClinicalAmberContainer,
            ClinicalAmberOnContainer,
            ClinicalAmberTertiary,
            Icons.Default.Warning
        )
        AlertSeverity.PERINGATAN -> Quadruple(
            MedicalCyanContainer,
            MedicalCyanOnContainer,
            MedicalCyanSecondary,
            Icons.Default.Info
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(containerBg)
            .border(1.5.dp, borderColor.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .padding(14.dp)
            .testTag("alert_card_${alert.id}")
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = alert.severity.label,
                        tint = borderColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = alert.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = contentColor
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(borderColor)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = alert.severity.label.split(" ").first(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Pesakit: ${alert.patientName} (${alert.regNumber})",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = contentColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = alert.message,
                style = MaterialTheme.typography.bodySmall,
                color = contentColor.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Protocol Guidance Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.7f))
                    .padding(8.dp)
            ) {
                Column {
                    Text(
                        text = "\u26A0\uFE0F Protokol Tindakan KKM:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = borderColor
                    )
                    Text(
                        text = alert.protocolAction,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        color = Color(0xFF1E293B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { onViewPatient(alert.patientId) },
                    modifier = Modifier.testTag("alert_view_patient_btn_${alert.id}")
                ) {
                    Text("Lihat Pesakit", style = MaterialTheme.typography.labelMedium)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onActionClick,
                    colors = ButtonDefaults.buttonColors(containerColor = borderColor),
                    modifier = Modifier.testTag("alert_action_btn_${alert.id}")
                ) {
                    val actionLabel = when (alert.severity) {
                        AlertSeverity.KRITIKAL -> "Semakan Doktor"
                        AlertSeverity.AMARAN_SEDERHANA -> "Tindakan Kepatuhan"
                        AlertSeverity.PERINGATAN -> "Jadualkan UDS"
                    }
                    Text(actionLabel, style = MaterialTheme.typography.labelMedium, color = Color.White)
                }
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
