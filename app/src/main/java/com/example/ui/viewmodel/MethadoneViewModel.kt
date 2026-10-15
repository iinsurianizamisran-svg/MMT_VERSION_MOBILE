package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AdherenceStats
import com.example.data.model.AppointmentStatus
import com.example.data.model.AppointmentType
import com.example.data.model.ClinicalAlert
import com.example.data.model.DispensationRecord
import com.example.data.model.DispensationStatus
import com.example.data.model.FollowUpAppointment
import com.example.data.model.Patient
import com.example.data.model.PatientStatus
import com.example.data.model.RegimenType
import com.example.data.model.TreatmentPhase
import com.example.data.repository.MethadoneRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class MethadoneUiState(
    val patients: List<Patient> = emptyList(),
    val dispensations: List<DispensationRecord> = emptyList(),
    val appointments: List<FollowUpAppointment> = emptyList(),
    val alerts: List<ClinicalAlert> = emptyList(),
    val stats: AdherenceStats = AdherenceStats(0, 0, 0, 0f, 0, 0, 0, 0),
    val searchQuery: String = "",
    val regimenFilter: RegimenType? = null,
    val selectedTab: Int = 0,
    val selectedPatientForDetail: Patient? = null,
    val isNewPatientDialogOpen: Boolean = false,
    val isDispenseDialogOpen: Boolean = false,
    val dispenseTargetPatient: Patient? = null,
    val isNewAppointmentDialogOpen: Boolean = false,
    val isUdsDialogOpen: Boolean = false,
    val udsTargetPatient: Patient? = null,
    val userNotice: String? = null
)

class MethadoneViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = MethadoneRepository(database)

    private val _searchQuery = MutableStateFlow("")
    private val _regimenFilter = MutableStateFlow<RegimenType?>(null)
    private val _selectedTab = MutableStateFlow(0)
    private val _selectedPatientForDetail = MutableStateFlow<Patient?>(null)
    private val _isNewPatientDialogOpen = MutableStateFlow(false)
    private val _isDispenseDialogOpen = MutableStateFlow(false)
    private val _dispenseTargetPatient = MutableStateFlow<Patient?>(null)
    private val _isNewAppointmentDialogOpen = MutableStateFlow(false)
    private val _isUdsDialogOpen = MutableStateFlow(false)
    private val _udsTargetPatient = MutableStateFlow<Patient?>(null)
    private val _userNotice = MutableStateFlow<String?>(null)

    val uiState: StateFlow<MethadoneUiState> = combine(
        repository.patients,
        repository.dispensations,
        repository.appointments,
        repository.clinicalAlerts,
        repository.adherenceStats
    ) { pList, dList, aList, alertList, statData ->
        MethadoneUiState(
            patients = pList,
            dispensations = dList,
            appointments = aList,
            alerts = alertList,
            stats = statData
        )
    }.combine(
        combine(
            _searchQuery,
            _regimenFilter,
            _selectedTab,
            _selectedPatientForDetail,
            _userNotice
        ) { search, filter, tab, patientDetail, notice ->
            Tuple5(search, filter, tab, patientDetail, notice)
        }
    ) { baseState, tuple ->
        baseState.copy(
            searchQuery = tuple.a,
            regimenFilter = tuple.b,
            selectedTab = tuple.c,
            selectedPatientForDetail = tuple.d,
            userNotice = tuple.e,
            isNewPatientDialogOpen = _isNewPatientDialogOpen.value,
            isDispenseDialogOpen = _isDispenseDialogOpen.value,
            dispenseTargetPatient = _dispenseTargetPatient.value,
            isNewAppointmentDialogOpen = _isNewAppointmentDialogOpen.value,
            isUdsDialogOpen = _isUdsDialogOpen.value,
            udsTargetPatient = _udsTargetPatient.value
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        MethadoneUiState()
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setRegimenFilter(filter: RegimenType?) {
        _regimenFilter.value = filter
    }

    fun setSelectedTab(index: Int) {
        _selectedTab.value = index
    }

    fun selectPatientForDetail(patient: Patient?) {
        _selectedPatientForDetail.value = patient
    }

    fun openNewPatientDialog() {
        _isNewPatientDialogOpen.value = true
    }

    fun closeNewPatientDialog() {
        _isNewPatientDialogOpen.value = false
    }

    fun openDispenseDialog(patient: Patient) {
        _dispenseTargetPatient.value = patient
        _isDispenseDialogOpen.value = true
    }

    fun closeDispenseDialog() {
        _isDispenseDialogOpen.value = false
        _dispenseTargetPatient.value = null
    }

    fun openNewAppointmentDialog() {
        _isNewAppointmentDialogOpen.value = true
    }

    fun closeNewAppointmentDialog() {
        _isNewAppointmentDialogOpen.value = false
    }

    fun openUdsDialog(patient: Patient) {
        _udsTargetPatient.value = patient
        _isUdsDialogOpen.value = true
    }

    fun closeUdsDialog() {
        _isUdsDialogOpen.value = false
        _udsTargetPatient.value = null
    }

    fun clearNotice() {
        _userNotice.value = null
    }

    fun recordDispensation(
        patient: Patient,
        status: DispensationStatus,
        dotDoseMg: Double,
        dbbDoseMg: Double,
        dbbDays: Int,
        dispenserName: String,
        notes: String
    ) {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayStr = sdf.format(Date())

            val record = DispensationRecord(
                id = UUID.randomUUID().toString(),
                patientId = patient.id,
                patientName = patient.fullName,
                date = todayStr,
                timestamp = System.currentTimeMillis(),
                status = status,
                dotDoseMg = dotDoseMg,
                dbbDoseMg = dbbDoseMg,
                dbbDaysGiven = dbbDays,
                dispenserName = dispenserName.ifBlank { "Pegawai Farmasi" },
                staffNotes = notes
            )
            repository.recordDispensation(record)

            val msg = when (status) {
                DispensationStatus.HADIR_DOT -> "Rekod DOT ${patient.fullName} (${dotDoseMg}mg) berjaya disahkan."
                DispensationStatus.HADIR_DOT_DBB -> "Rekod DOT + $dbbDays Hari DBB (${patient.fullName}) berjaya direkodkan."
                DispensationStatus.TIDAK_HADIR_MISSED -> "Pesakit ${patient.fullName} ditanda TIDAK HADIR. Sistem amaran diaktifkan."
                DispensationStatus.DITANGGUH_KLINIKAL -> "Dispensi ${patient.fullName} ditangguhkan atas sebab klinikal."
            }
            _userNotice.value = msg
            closeDispenseDialog()
        }
    }

    fun addNewPatient(
        name: String,
        ic: String,
        regNum: String,
        age: Int,
        phone: String,
        emergency: String,
        regimen: RegimenType,
        doseMg: Double,
        phase: TreatmentPhase,
        dbbDays: Int,
        notes: String
    ) {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayStr = sdf.format(Date())

            val newPatient = Patient(
                id = UUID.randomUUID().toString(),
                regNumber = regNum.ifBlank { "MTM/2026/${(100..999).random()}" },
                fullName = name,
                icNumber = ic,
                age = age,
                phone = phone,
                emergencyContact = emergency,
                startDate = todayStr,
                regimen = regimen,
                currentDoseMg = doseMg,
                phase = phase,
                status = PatientStatus.AKTIF,
                dbbDaysAllowed = if (regimen == RegimenType.DOT_DBB) dbbDays else 0,
                lastUdsDate = todayStr,
                lastUdsResult = "Negatif (Saringan Awal)",
                notes = notes
            )
            repository.addPatient(newPatient)
            _userNotice.value = "Pesakit ${newPatient.fullName} berjaya didaftarkan. Janji temu susulan automatik dijadualkan."
            closeNewPatientDialog()
        }
    }

    fun recordUds(patientId: String, result: String) {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayStr = sdf.format(Date())
            repository.updateUdsResult(patientId, result, todayStr)
            _userNotice.value = "Keputusan ujian UDS berjaya dikemaskini: $result"
            closeUdsDialog()
        }
    }

    fun createAppointment(
        patient: Patient,
        type: AppointmentType,
        date: String,
        time: String,
        notes: String
    ) {
        viewModelScope.launch {
            val apt = FollowUpAppointment(
                id = UUID.randomUUID().toString(),
                patientId = patient.id,
                patientName = patient.fullName,
                regNumber = patient.regNumber,
                appointmentType = type,
                scheduledDate = date,
                scheduledTime = time,
                status = AppointmentStatus.AKAN_DATANG,
                doctorNotes = notes,
                isAutoGenerated = false
            )
            repository.addAppointment(apt)
            _userNotice.value = "Janji temu baharu untuk ${patient.fullName} pada $date berjaya didaftarkan."
            closeNewAppointmentDialog()
        }
    }

    fun completeAppointment(appointment: FollowUpAppointment) {
        viewModelScope.launch {
            val updated = appointment.copy(status = AppointmentStatus.SELESAI)
            repository.updateAppointment(updated)
            _userNotice.value = "Janji temu ${appointment.patientName} (${appointment.appointmentType.label}) disahkan selesai."
        }
    }

    fun autoScheduleAllPatients() {
        viewModelScope.launch {
            val patientsList = uiState.value.patients.filter { it.status == PatientStatus.AKTIF }
            var scheduledCount = 0
            for (p in patientsList) {
                repository.autoScheduleForPatient(p)
                scheduledCount++
            }
            _userNotice.value = "Berjaya menjana janji temu susulan automatik untuk $scheduledCount pesakit aktif."
        }
    }
}

private data class Tuple5<A, B, C, D, E>(
    val a: A,
    val b: B,
    val c: C,
    val d: D,
    val e: E
)
