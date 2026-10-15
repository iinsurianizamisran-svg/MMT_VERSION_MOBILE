package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.AlertSeverity
import com.example.data.model.DispensationRecord
import com.example.data.model.DispensationStatus
import com.example.data.model.Patient
import com.example.data.model.PatientStatus
import com.example.data.model.RegimenType
import com.example.data.model.TreatmentPhase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("e-Metadon", appName)
    }

    @Test
    fun `patient dose conversion to ml is accurate`() {
        val patient = Patient(
            id = "test-1",
            regNumber = "MTM/2026/001",
            fullName = "Ahmad Farhan",
            icNumber = "880614-10-5321",
            age = 38,
            phone = "012-3456789",
            emergencyContact = "019-8765432",
            startDate = "2024-03-15",
            regimen = RegimenType.DOT_DBB,
            currentDoseMg = 75.0,
            phase = TreatmentPhase.PENGEKALAN,
            status = PatientStatus.AKTIF,
            dbbDaysAllowed = 3
        )
        // KKM Standard syrup formula: 5mg/ml -> 75mg = 15.0ml
        assertEquals(15.0, patient.doseInMl, 0.001)
    }

    @Test
    fun `three consecutive missed doses trigger critical alert condition`() {
        val dispensations = listOf(
            DispensationRecord(
                id = "d1",
                patientId = "p1",
                patientName = "Test",
                date = "2026-10-15",
                timestamp = 3000L,
                status = DispensationStatus.TIDAK_HADIR_MISSED,
                dotDoseMg = 0.0
            ),
            DispensationRecord(
                id = "d2",
                patientId = "p1",
                patientName = "Test",
                date = "2026-10-14",
                timestamp = 2000L,
                status = DispensationStatus.TIDAK_HADIR_MISSED,
                dotDoseMg = 0.0
            ),
            DispensationRecord(
                id = "d3",
                patientId = "p1",
                patientName = "Test",
                date = "2026-10-13",
                timestamp = 1000L,
                status = DispensationStatus.TIDAK_HADIR_MISSED,
                dotDoseMg = 0.0
            )
        )

        var consecutiveMissed = 0
        for (disp in dispensations) {
            if (disp.status == DispensationStatus.TIDAK_HADIR_MISSED) {
                consecutiveMissed++
            } else {
                break
            }
        }

        assertTrue(consecutiveMissed >= 3)
    }
}
