package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DispensationStatus
import com.example.data.model.Patient
import com.example.ui.components.DispenseDialog
import com.example.ui.components.DoctorSchedulingConfigDialog
import com.example.ui.components.NewAppointmentDialog
import com.example.ui.components.NewPatientDialog
import com.example.ui.components.PatientDetailDialog
import com.example.ui.components.UdsDialog
import com.example.ui.screens.AppointmentsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DispensationScreen
import com.example.ui.screens.PatientsScreen
import com.example.ui.theme.ClinicalCriticalRed
import com.example.ui.theme.ClinicalTealContainer
import com.example.ui.theme.ClinicalTealOnContainer
import com.example.ui.theme.ClinicalTealPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MethadoneViewModel

import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import com.example.ui.auth.AuthScreen
import com.example.ui.auth.attemptAutoSignIn
import com.example.ui.auth.performSignOut
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    private val viewModel: MethadoneViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val coroutineScope = rememberCoroutineScope()
                val credentialManager = remember { CredentialManager.create(context) }
                var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }

                DisposableEffect(Unit) {
                    val listener = FirebaseAuth.AuthStateListener { auth ->
                        currentUser = auth.currentUser
                        currentUser?.uid?.let { uid ->
                            viewModel.initForUser(uid)
                        }
                    }
                    Firebase.auth.addAuthStateListener(listener)
                    onDispose {
                        Firebase.auth.removeAuthStateListener(listener)
                    }
                }

                LaunchedEffect(Unit) {
                    if (currentUser == null) {
                        attemptAutoSignIn(
                            context = context,
                            credentialManager = credentialManager,
                            onAuthSuccess = {
                                currentUser = Firebase.auth.currentUser
                                currentUser?.uid?.let { uid ->
                                    viewModel.initForUser(uid)
                                }
                            },
                            onUnauthenticated = {},
                            scope = coroutineScope
                        )
                    } else {
                        currentUser?.uid?.let { uid ->
                            viewModel.initForUser(uid)
                        }
                    }
                }

                if (currentUser == null) {
                    AuthScreen(
                        onAuthSuccess = {
                            currentUser = Firebase.auth.currentUser
                            currentUser?.uid?.let { uid ->
                                viewModel.initForUser(uid)
                            }
                        }
                    )
                } else {
                    MethadoneApp(
                        viewModel = viewModel,
                        currentUser = currentUser,
                        onSignOut = {
                            performSignOut(
                                context = context,
                                credentialManager = credentialManager,
                                onSignOutComplete = {
                                    currentUser = null
                                },
                                scope = coroutineScope
                            )
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MethadoneApp(
    viewModel: MethadoneViewModel,
    currentUser: FirebaseUser? = null,
    onSignOut: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show notices in Snackbar
    LaunchedEffect(uiState.userNotice) {
        uiState.userNotice?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearNotice()
        }
    }

    // BackHandler: If patient detail is open, close it; else if not on Dashboard (tab 0), return to tab 0
    BackHandler(enabled = uiState.selectedPatientForDetail != null || uiState.selectedTab != 0) {
        if (uiState.selectedPatientForDetail != null) {
            viewModel.selectPatientForDetail(null)
        } else if (uiState.selectedTab != 0) {
            viewModel.setSelectedTab(0)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(ClinicalTealPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalHospital,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "e-Metadon",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Pengurusan Terapi Gantian Metadon",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    if (uiState.alerts.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.setSelectedTab(0) },
                            modifier = Modifier.testTag("btn_top_alert_indicator")
                        ) {
                            BadgedBox(
                                badge = {
                                    Badge(containerColor = ClinicalCriticalRed) {
                                        Text("${uiState.alerts.size}")
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Analytics,
                                    contentDescription = "Amaran Kepatuhan",
                                    tint = ClinicalCriticalRed
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onSignOut,
                        modifier = Modifier.testTag("btn_top_sign_out")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Log Keluar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = ClinicalTealPrimary
            ) {
                NavigationBarItem(
                    selected = uiState.selectedTab == 0,
                    onClick = { viewModel.setSelectedTab(0) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (uiState.alerts.isNotEmpty()) {
                                    Badge(containerColor = ClinicalCriticalRed) {
                                        Text("${uiState.alerts.size}")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                if (uiState.selectedTab == 0) Icons.Default.Analytics else Icons.Outlined.Analytics,
                                contentDescription = "Dashboard"
                            )
                        }
                    },
                    label = { Text("Analitik", fontSize = 11.sp, fontWeight = if (uiState.selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ClinicalTealPrimary,
                        selectedTextColor = ClinicalTealPrimary,
                        indicatorColor = ClinicalTealContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_dashboard")
                )

                NavigationBarItem(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.setSelectedTab(1) },
                    icon = {
                        val lateReminders = uiState.staffDispensingReminders.count {
                            it.status == com.example.data.model.DispensingAlertStatus.LATE_WARNING ||
                                    it.status == com.example.data.model.DispensingAlertStatus.CRITICAL_TOLERANCE_LOSS
                        }
                        BadgedBox(
                            badge = {
                                if (lateReminders > 0) {
                                    Badge(containerColor = ClinicalCriticalRed) {
                                        Text("$lateReminders")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                if (uiState.selectedTab == 1) Icons.Default.Medication else Icons.Outlined.Medication,
                                contentDescription = "Dispensi"
                            )
                        }
                    },
                    label = { Text("Dispensi", fontSize = 11.sp, fontWeight = if (uiState.selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ClinicalTealPrimary,
                        selectedTextColor = ClinicalTealPrimary,
                        indicatorColor = ClinicalTealContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_dispense")
                )

                NavigationBarItem(
                    selected = uiState.selectedTab == 2,
                    onClick = { viewModel.setSelectedTab(2) },
                    icon = {
                        Icon(
                            if (uiState.selectedTab == 2) Icons.Default.People else Icons.Outlined.People,
                            contentDescription = "Pesakit"
                        )
                    },
                    label = { Text("Pesakit", fontSize = 11.sp, fontWeight = if (uiState.selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ClinicalTealPrimary,
                        selectedTextColor = ClinicalTealPrimary,
                        indicatorColor = ClinicalTealContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_patients")
                )

                NavigationBarItem(
                    selected = uiState.selectedTab == 3,
                    onClick = { viewModel.setSelectedTab(3) },
                    icon = {
                        val overdueCount = uiState.appointments.count { it.status == com.example.data.model.AppointmentStatus.TERTUNGGAK }
                        val suggestionsCount = uiState.suggestedAppointments.count { !it.isAlreadyScheduled }
                        BadgedBox(
                            badge = {
                                if (overdueCount > 0 || suggestionsCount > 0) {
                                    Badge(containerColor = if (overdueCount > 0) ClinicalCriticalRed else ClinicalTealPrimary) {
                                        Text("${overdueCount + suggestionsCount}")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                if (uiState.selectedTab == 3) Icons.Default.Event else Icons.Outlined.Event,
                                contentDescription = "Janji Temu"
                            )
                        }
                    },
                    label = { Text("Temujanji", fontSize = 11.sp, fontWeight = if (uiState.selectedTab == 3) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ClinicalTealPrimary,
                        selectedTextColor = ClinicalTealPrimary,
                        indicatorColor = ClinicalTealContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_appointments")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.selectedTab) {
                0 -> DashboardScreen(
                    state = uiState,
                    onViewPatient = { patient -> viewModel.selectPatientForDetail(patient) },
                    onAutoScheduleAll = { viewModel.autoScheduleAllPatients() },
                    onOpenDoctorConfig = { viewModel.openDoctorConfigDialog() },
                    onApplySuggestion = { suggestion -> viewModel.applySuggestedAppointment(suggestion) },
                    onNavigateToDispense = { viewModel.setSelectedTab(1) },
                    onNavigateToAppointments = { viewModel.setSelectedTab(3) },
                    onSelectAnalyticsTab = { idx -> viewModel.setActiveAnalyticsTab(idx) }
                )
                1 -> DispensationScreen(
                    state = uiState,
                    onDispenseClick = { patient -> viewModel.openDispenseDialog(patient) },
                    onQuickDotTaken = { patient ->
                        viewModel.recordDispensation(
                            patient = patient,
                            status = DispensationStatus.HADIR_DOT,
                            dotDoseMg = patient.currentDoseMg,
                            dbbDoseMg = 0.0,
                            dbbDays = 0,
                            dispenserName = "Pegawai Farmasi",
                            notes = "Minum habis di depan pegawai bertugas."
                        )
                    },
                    onMarkMissedDose = { patient ->
                        viewModel.recordDispensation(
                            patient = patient,
                            status = DispensationStatus.TIDAK_HADIR_MISSED,
                            dotDoseMg = 0.0,
                            dbbDoseMg = 0.0,
                            dbbDays = 0,
                            dispenserName = "Sistem Amaran",
                            notes = "Pesakit tidak hadir ke klinik hari ini."
                        )
                    },
                    onPatientClick = { patient -> viewModel.selectPatientForDetail(patient) },
                    onSendStaffReminder = { name, phone -> viewModel.sendStaffReminderNotice(name, phone) }
                )
                2 -> PatientsScreen(
                    state = uiState,
                    onPatientClick = { patient -> viewModel.selectPatientForDetail(patient) },
                    onDispenseClick = { patient -> viewModel.openDispenseDialog(patient) },
                    onUdsClick = { patient -> viewModel.openUdsDialog(patient) },
                    onAddNewPatient = { viewModel.openNewPatientDialog() }
                )
                3 -> AppointmentsScreen(
                    state = uiState,
                    onAutoScheduleAll = { viewModel.autoScheduleAllPatients() },
                    onOpenDoctorConfig = { viewModel.openDoctorConfigDialog() },
                    onApplySuggestion = { suggestion -> viewModel.applySuggestedAppointment(suggestion) },
                    onApplyAllSuggestions = { viewModel.applyAllSuggestedAppointments() },
                    onAddNewAppointment = { viewModel.openNewAppointmentDialog() },
                    onCompleteAppointment = { apt -> viewModel.completeAppointment(apt) }
                )
            }
        }
    }

    // Doctor Scheduling Configuration Dialog
    if (uiState.isDoctorConfigDialogOpen) {
        DoctorSchedulingConfigDialog(
            currentConfig = uiState.doctorConfig,
            onDismiss = { viewModel.closeDoctorConfigDialog() },
            onSave = { newConfig -> viewModel.updateDoctorSchedulingConfig(newConfig) }
        )
    }

    // New Patient Dialog
    if (uiState.isNewPatientDialogOpen) {
        NewPatientDialog(
            onDismiss = { viewModel.closeNewPatientDialog() },
            onSave = { name, ic, regNum, age, phone, emergency, regimen, dose, phase, dbbDays, notes ->
                viewModel.addNewPatient(
                    name = name,
                    ic = ic,
                    regNum = regNum,
                    age = age,
                    phone = phone,
                    emergency = emergency,
                    regimen = regimen,
                    doseMg = dose,
                    phase = phase,
                    dbbDays = dbbDays,
                    notes = notes
                )
            }
        )
    }

    // Dispense Dialog
    if (uiState.isDispenseDialogOpen && uiState.dispenseTargetPatient != null) {
        val target = uiState.dispenseTargetPatient!!
        val isCrit = uiState.alerts.any {
            it.patientId == target.id && it.severity == com.example.data.model.AlertSeverity.KRITIKAL
        }
        DispenseDialog(
            patient = target,
            isToleranceLossAlert = isCrit,
            onDismiss = { viewModel.closeDispenseDialog() },
            onConfirm = { status, dotMg, dbbMg, days, dispenser, notes ->
                viewModel.recordDispensation(
                    patient = target,
                    status = status,
                    dotDoseMg = dotMg,
                    dbbDoseMg = dbbMg,
                    dbbDays = days,
                    dispenserName = dispenser,
                    notes = notes
                )
            }
        )
    }

    // New Appointment Dialog
    if (uiState.isNewAppointmentDialogOpen) {
        NewAppointmentDialog(
            patients = uiState.patients,
            onDismiss = { viewModel.closeNewAppointmentDialog() },
            onSave = { patient, type, date, time, notes ->
                viewModel.createAppointment(patient, type, date, time, notes)
            }
        )
    }

    // UDS Recording Dialog
    if (uiState.isUdsDialogOpen && uiState.udsTargetPatient != null) {
        UdsDialog(
            patient = uiState.udsTargetPatient!!,
            onDismiss = { viewModel.closeUdsDialog() },
            onConfirm = { result ->
                viewModel.recordUds(uiState.udsTargetPatient!!.id, result)
            }
        )
    }

    // Patient Detail Dialog
    uiState.selectedPatientForDetail?.let { patient ->
        PatientDetailDialog(
            patient = patient,
            dispensationHistory = uiState.dispensations,
            onDismiss = { viewModel.selectPatientForDetail(null) },
            onDispenseClick = {
                viewModel.selectPatientForDetail(null)
                viewModel.openDispenseDialog(patient)
            },
            onUdsClick = {
                viewModel.selectPatientForDetail(null)
                viewModel.openUdsDialog(patient)
            },
            onAppointmentClick = {
                viewModel.selectPatientForDetail(null)
                viewModel.openNewAppointmentDialog()
            }
        )
    }
}
