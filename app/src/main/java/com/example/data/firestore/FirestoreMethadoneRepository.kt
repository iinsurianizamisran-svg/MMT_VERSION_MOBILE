package com.example.data.firestore

import android.content.Context
import com.example.R
import com.example.data.model.AppointmentStatus
import com.example.data.model.AppointmentType
import com.example.data.model.DispensationRecord
import com.example.data.model.DispensationStatus
import com.example.data.model.FollowUpAppointment
import com.example.data.model.Patient
import com.example.data.model.PatientStatus
import com.example.data.model.RegimenType
import com.example.data.model.TreatmentPhase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirestoreMethadoneRepository(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    // Primary injection constructor resolving named database ID from string resources
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        FirebaseAuth.getInstance()
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    private fun patientsCollection(userId: String) =
        firestore.collection("users").document(userId).collection("patients")

    private fun dispensationsCollection(userId: String) =
        firestore.collection("users").document(userId).collection("dispensations")

    private fun appointmentsCollection(userId: String) =
        firestore.collection("users").document(userId).collection("appointments")

    // =========================================================================
    // PATIENTS
    // =========================================================================

    fun observePatients(): Flow<List<Patient>> = flow {
        val uid = requireUserId()
        val path = "users/$uid/patients"
        patientsCollection(uid).snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    try {
                        val regimenStr = data["regimenType"] as? String ?: "DOT"
                        val phaseStr = data["treatmentPhase"] as? String ?: "PENSTABILAN"
                        Patient(
                            id = doc.id,
                            regNumber = data["methadoneRegNo"] as? String ?: "",
                            fullName = data["name"] as? String ?: "",
                            icNumber = data["icNumber"] as? String ?: "",
                            age = (data["age"] as? Number)?.toInt() ?: 30,
                            phone = data["phone"] as? String ?: "",
                            emergencyContact = data["nextOfKinName"] as? String ?: "",
                            startDate = data["therapyStartDate"] as? String ?: "2024-01-01",
                            regimen = if (regimenStr == "DOT_DBB") RegimenType.DOT_DBB else RegimenType.DOT,
                            currentDoseMg = (data["currentDoseMg"] as? Number)?.toDouble() ?: 60.0,
                            phase = try { TreatmentPhase.valueOf(phaseStr) } catch (_: Exception) { TreatmentPhase.PENSTABILAN },
                            status = PatientStatus.AKTIF,
                            dbbDaysAllowed = (data["dbbDaysPerWeek"] as? Number)?.toInt() ?: 0,
                            lastUdsDate = data["udsLastTestedDate"] as? String ?: "",
                            lastUdsResult = data["udsStatus"] as? String ?: "Negatif",
                            notes = ""
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
            }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                throw error
            }
            .collect { emit(it) }
    }

    suspend fun savePatient(patient: Patient): Result<String> {
        val uid = requireUserId()
        val path = "users/$uid/patients/${patient.id}"
        val docRef = patientsCollection(uid).document(patient.id)
        val payload = hashMapOf<String, Any>(
            "userId" to uid,
            "name" to patient.fullName,
            "icNumber" to patient.icNumber,
            "methadoneRegNo" to patient.regNumber,
            "age" to patient.age,
            "phone" to patient.phone,
            "nextOfKinName" to patient.emergencyContact,
            "nextOfKinPhone" to patient.phone,
            "regimenType" to if (patient.regimen == RegimenType.DOT_DBB) "DOT_DBB" else "DOT",
            "dbbDaysPerWeek" to patient.dbbDaysAllowed,
            "currentDoseMg" to patient.currentDoseMg,
            "currentDoseMl" to patient.doseInMl,
            "treatmentPhase" to patient.phase.name,
            "therapyStartDate" to patient.startDate,
            "udsStatus" to patient.lastUdsResult,
            "udsLastTestedDate" to patient.lastUdsDate,
            "consecutiveMissedDays" to 0,
            "adherenceRate" to 100.0
        )
        return try {
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun updatePatientUds(patientId: String, result: String, date: String): Result<Unit> {
        val uid = requireUserId()
        val path = "users/$uid/patients/$patientId"
        val docRef = patientsCollection(uid).document(patientId)
        return try {
            docRef.update(
                mapOf(
                    "udsStatus" to result,
                    "udsLastTestedDate" to date
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    // =========================================================================
    // DISPENSATIONS
    // =========================================================================

    fun observeDispensations(): Flow<List<DispensationRecord>> = flow {
        val uid = requireUserId()
        val path = "users/$uid/dispensations"
        dispensationsCollection(uid).snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    try {
                        val statusStr = data["status"] as? String ?: "DISPENSED_DOT"
                        val dispStatus = when (statusStr) {
                            "DISPENSED_DOT" -> DispensationStatus.HADIR_DOT
                            "DISPENSED_DOT_DBB" -> DispensationStatus.HADIR_DOT_DBB
                            "MISSED" -> DispensationStatus.TIDAK_HADIR_MISSED
                            "DEFERRED" -> DispensationStatus.DITANGGUH_KLINIKAL
                            else -> DispensationStatus.HADIR_DOT
                        }
                        DispensationRecord(
                            id = doc.id,
                            patientId = data["patientId"] as? String ?: "",
                            patientName = data["patientName"] as? String ?: "",
                            date = data["date"] as? String ?: "",
                            timestamp = (data["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                            status = dispStatus,
                            dotDoseMg = (data["doseMg"] as? Number)?.toDouble() ?: 0.0,
                            dbbDoseMg = (data["dbbDoseMg"] as? Number)?.toDouble() ?: 0.0,
                            dbbDaysGiven = (data["dbbBottlesGiven"] as? Number)?.toInt() ?: 0,
                            dispenserName = data["dispensedBy"] as? String ?: "Pegawai Farmasi",
                            staffNotes = data["notes"] as? String ?: ""
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
            }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                throw error
            }
            .collect { emit(it) }
    }

    suspend fun saveDispensation(record: DispensationRecord): Result<String> {
        val uid = requireUserId()
        val path = "users/$uid/dispensations/${record.id}"
        val docRef = dispensationsCollection(uid).document(record.id)
        val statusStr = when (record.status) {
            DispensationStatus.HADIR_DOT -> "DISPENSED_DOT"
            DispensationStatus.HADIR_DOT_DBB -> "DISPENSED_DOT_DBB"
            DispensationStatus.TIDAK_HADIR_MISSED -> "MISSED"
            DispensationStatus.DITANGGUH_KLINIKAL -> "DEFERRED"
        }
        val payload = hashMapOf<String, Any>(
            "userId" to uid,
            "patientId" to record.patientId,
            "patientName" to record.patientName,
            "status" to statusStr,
            "date" to record.date,
            "doseMg" to record.dotDoseMg,
            "dbbDoseMg" to record.dbbDoseMg,
            "dbbBottlesGiven" to record.dbbDaysGiven,
            "dispensedBy" to record.dispenserName,
            "notes" to record.staffNotes,
            "timestamp" to record.timestamp
        )
        return try {
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    // =========================================================================
    // APPOINTMENTS
    // =========================================================================

    fun observeAppointments(): Flow<List<FollowUpAppointment>> = flow {
        val uid = requireUserId()
        val path = "users/$uid/appointments"
        appointmentsCollection(uid).snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    try {
                        val typeStr = data["appointmentType"] as? String ?: "DOCTOR_REVIEW"
                        val appType = when (typeStr) {
                            "DOCTOR_REVIEW" -> AppointmentType.SEMAKAN_DOKTOR
                            "UDS_SCREENING" -> AppointmentType.UJIAN_UDS
                            "DOSE_TITRATION" -> AppointmentType.PENILAIAN_DBB
                            "COUNSELING" -> AppointmentType.KAUNSELING
                            else -> AppointmentType.SEMAKAN_DOKTOR
                        }
                        val statusStr = data["status"] as? String ?: "SCHEDULED"
                        val appStatus = when (statusStr) {
                            "SCHEDULED" -> AppointmentStatus.AKAN_DATANG
                            "COMPLETED" -> AppointmentStatus.SELESAI
                            "MISSED" -> AppointmentStatus.TERTUNGGAK
                            "RESCHEDULED" -> AppointmentStatus.DIBATALKAN
                            else -> AppointmentStatus.AKAN_DATANG
                        }
                        FollowUpAppointment(
                            id = doc.id,
                            patientId = data["patientId"] as? String ?: "",
                            patientName = data["patientName"] as? String ?: "",
                            regNumber = data["methadoneRegNo"] as? String ?: "",
                            appointmentType = appType,
                            scheduledDate = data["scheduledDate"] as? String ?: "",
                            scheduledTime = data["scheduledTime"] as? String ?: "09:00 AM",
                            status = appStatus,
                            doctorNotes = data["clinicalNotes"] as? String ?: "",
                            isAutoGenerated = data["isAutoScheduled"] as? Boolean ?: false
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
            }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                throw error
            }
            .collect { emit(it) }
    }

    suspend fun saveAppointment(apt: FollowUpAppointment): Result<String> {
        val uid = requireUserId()
        val path = "users/$uid/appointments/${apt.id}"
        val docRef = appointmentsCollection(uid).document(apt.id)
        val typeStr = when (apt.appointmentType) {
            AppointmentType.SEMAKAN_DOKTOR -> "DOCTOR_REVIEW"
            AppointmentType.UJIAN_UDS -> "UDS_SCREENING"
            AppointmentType.PENILAIAN_DBB -> "DOSE_TITRATION"
            AppointmentType.KAUNSELING -> "COUNSELING"
        }
        val statusStr = when (apt.status) {
            AppointmentStatus.AKAN_DATANG -> "SCHEDULED"
            AppointmentStatus.SELESAI -> "COMPLETED"
            AppointmentStatus.TERTUNGGAK -> "MISSED"
            AppointmentStatus.DIBATALKAN -> "RESCHEDULED"
        }
        val payload = hashMapOf<String, Any>(
            "userId" to uid,
            "patientId" to apt.patientId,
            "patientName" to apt.patientName,
            "methadoneRegNo" to apt.regNumber,
            "appointmentType" to typeStr,
            "scheduledDate" to apt.scheduledDate,
            "scheduledTime" to apt.scheduledTime,
            "status" to statusStr,
            "clinicalNotes" to apt.doctorNotes,
            "isAutoScheduled" to apt.isAutoGenerated
        )
        return try {
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun seedInitialDataIfEmpty(uid: String) {
        val existing = patientsCollection(uid).limit(1).get().await()
        if (!existing.isEmpty) return

        val samplePatients = listOf(
            Patient(
                id = "pat_1",
                regNumber = "MTH-2024-001",
                fullName = "Mohd Rizal bin Othman",
                icNumber = "850312-10-5431",
                age = 39,
                phone = "012-3456789",
                emergencyContact = "Siti Aminah (Isteri) 012-9876543",
                startDate = "2023-04-10",
                regimen = RegimenType.DOT_DBB,
                currentDoseMg = 75.0,
                phase = TreatmentPhase.PENGEKALAN,
                status = PatientStatus.AKTIF,
                dbbDaysAllowed = 3,
                lastUdsDate = "2026-09-18",
                lastUdsResult = "Negatif"
            ),
            Patient(
                id = "pat_2",
                regNumber = "MTH-2024-002",
                fullName = "Ahmad Faiz bin Zakaria",
                icNumber = "910722-14-6129",
                age = 33,
                phone = "013-8877665",
                emergencyContact = "Zakaria (Bapa) 019-2233445",
                startDate = "2024-01-15",
                regimen = RegimenType.DOT,
                currentDoseMg = 50.0,
                phase = TreatmentPhase.PENSTABILAN,
                status = PatientStatus.AKTIF,
                dbbDaysAllowed = 0,
                lastUdsDate = "2026-08-01",
                lastUdsResult = "Negatif"
            ),
            Patient(
                id = "pat_3",
                regNumber = "MTH-2024-003",
                fullName = "Chong Wei Lun",
                icNumber = "881105-08-5913",
                age = 36,
                phone = "017-6543210",
                emergencyContact = "Chong Mei (Kakak) 016-5544332",
                startDate = "2024-02-20",
                regimen = RegimenType.DOT,
                currentDoseMg = 65.0,
                phase = TreatmentPhase.PENSTABILAN,
                status = PatientStatus.AKTIF,
                dbbDaysAllowed = 0,
                lastUdsDate = "2026-09-10",
                lastUdsResult = "Negatif"
            ),
            Patient(
                id = "pat_4",
                regNumber = "MTH-2024-004",
                fullName = "Suresh a/l Subramaniam",
                icNumber = "930415-05-5201",
                age = 31,
                phone = "019-7711223",
                emergencyContact = "Kavitha (Isteri) 011-3322110",
                startDate = "2024-05-01",
                regimen = RegimenType.DOT,
                currentDoseMg = 40.0,
                phase = TreatmentPhase.INDUKSI,
                status = PatientStatus.AKTIF,
                dbbDaysAllowed = 0,
                lastUdsDate = "2026-05-01",
                lastUdsResult = "Negatif"
            )
        )

        for (p in samplePatients) {
            savePatient(p)
        }
    }
}
