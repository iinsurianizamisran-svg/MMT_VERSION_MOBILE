package com.example.data.firestore

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.AppointmentType
import com.example.data.model.DispensationRecord
import com.example.data.model.DispensationStatus
import com.example.data.model.FollowUpAppointment
import com.example.data.model.Patient
import com.example.data.model.PatientStatus
import com.example.data.model.RegimenType
import com.example.data.model.TreatmentPhase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertTrue
import org.junit.Test

class MethadoneRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun authenticatedOwner_canSaveAndObservePatient(): Unit = runBlocking {
        val uid = signInTestUser("doctor_test@kkm.gov.my")
        val repo = FirestoreMethadoneRepository(firestore, auth)

        val patient = Patient(
            id = "pat_test_1",
            regNumber = "MTH-2024-TEST",
            fullName = "Ujian Pesakit",
            icNumber = "900101-14-1234",
            age = 34,
            phone = "012-3456789",
            emergencyContact = "Waris 012-9876543",
            startDate = "2024-01-01",
            regimen = RegimenType.DOT,
            currentDoseMg = 60.0,
            phase = TreatmentPhase.PENSTABILAN,
            status = PatientStatus.AKTIF
        )

        val saveResult = repo.savePatient(patient)
        assertTrue(saveResult.isSuccess)

        val patients = withTimeout(5000L) {
            repo.observePatients().first()
        }
        assertTrue(patients.any { it.id == "pat_test_1" })
    }

    @Test
    fun authenticatedOwner_canSaveDispensationAndAppointment(): Unit = runBlocking {
        val uid = signInTestUser("doctor_disp@kkm.gov.my")
        val repo = FirestoreMethadoneRepository(firestore, auth)

        val disp = DispensationRecord(
            id = "disp_test_1",
            patientId = "pat_test_1",
            patientName = "Ujian Pesakit",
            date = "2026-10-15",
            timestamp = System.currentTimeMillis(),
            status = DispensationStatus.HADIR_DOT,
            dotDoseMg = 60.0
        )
        val saveDispResult = repo.saveDispensation(disp)
        assertTrue(saveDispResult.isSuccess)

        val apt = FollowUpAppointment(
            id = "apt_test_1",
            patientId = "pat_test_1",
            patientName = "Ujian Pesakit",
            regNumber = "MTH-2024-TEST",
            appointmentType = AppointmentType.SEMAKAN_DOKTOR,
            scheduledDate = "2026-11-15"
        )
        val saveAptResult = repo.saveAppointment(apt)
        assertTrue(saveAptResult.isSuccess)
    }

    @Test
    fun unauthenticatedAccess_throwsOrFails(): Unit = runBlocking {
        signInTestUser("user_to_signout@kkm.gov.my")
        val repo = FirestoreMethadoneRepository(firestore, auth)
        auth.signOut()

        var caught = false
        try {
            val patient = Patient(
                id = "pat_unauth",
                regNumber = "MTH-UNAUTH",
                fullName = "Unauth",
                icNumber = "999999-99-9999",
                age = 25,
                phone = "010-0000000",
                emergencyContact = "None",
                startDate = "2024-01-01",
                regimen = RegimenType.DOT,
                currentDoseMg = 50.0,
                phase = TreatmentPhase.INDUKSI,
                status = PatientStatus.AKTIF
            )
            repo.savePatient(patient)
        } catch (e: IllegalStateException) {
            caught = true
        }
        assertTrue(caught)
    }
}
