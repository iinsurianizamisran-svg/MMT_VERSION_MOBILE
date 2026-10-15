package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AppointmentStatus
import com.example.data.model.AppointmentType
import com.example.data.model.DispensationStatus
import com.example.data.model.PatientStatus
import com.example.data.model.RegimenType
import com.example.data.model.TreatmentPhase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Database(
    entities = [PatientEntity::class, DispensationEntity::class, AppointmentEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun dispensationDao(): DispensationDao
    abstract fun appointmentDao(): AppointmentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "methadone_clinic.db"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(database: AppDatabase) {
            val pDao = database.patientDao()
            val dDao = database.dispensationDao()
            val aDao = database.appointmentDao()

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val cal = Calendar.getInstance()
            val todayStr = sdf.format(cal.time)

            cal.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayStr = sdf.format(cal.time)

            cal.add(Calendar.DAY_OF_YEAR, -1)
            val day2Str = sdf.format(cal.time)

            cal.add(Calendar.DAY_OF_YEAR, -1)
            val day3Str = sdf.format(cal.time)

            cal.time = Date()
            cal.add(Calendar.DAY_OF_YEAR, 3)
            val nextDate1 = sdf.format(cal.time)
            cal.add(Calendar.DAY_OF_YEAR, 4)
            val nextDate2 = sdf.format(cal.time)

            val p1 = PatientEntity(
                id = "p-1",
                regNumber = "MTM/2026/001",
                fullName = "Ahmad Farhan bin Rosli",
                icNumber = "880614-10-5321",
                age = 38,
                phone = "012-3456789",
                emergencyContact = "019-8765432 (Isteri - Siti)",
                startDate = "2024-03-15",
                regimen = RegimenType.DOT_DBB,
                currentDoseMg = 75.0,
                phase = TreatmentPhase.PENGEKALAN,
                status = PatientStatus.AKTIF,
                dbbDaysAllowed = 3,
                lastUdsDate = todayStr,
                lastUdsResult = "Negatif (Bersih)",
                notes = "Pesakit patuh & berdisiplin tinggi. Bekerja sebagai mekanik."
            )

            val p2 = PatientEntity(
                id = "p-2",
                regNumber = "MTM/2026/014",
                fullName = "Mohd Syazwan bin Ismail",
                icNumber = "950212-08-5901",
                age = 31,
                phone = "017-6543210",
                emergencyContact = "013-4455667 (Ibu)",
                startDate = "2025-08-10",
                regimen = RegimenType.DOT,
                currentDoseMg = 60.0,
                phase = TreatmentPhase.PENSTABILAN,
                status = PatientStatus.AKTIF,
                dbbDaysAllowed = 0,
                lastUdsDate = yesterdayStr,
                lastUdsResult = "Negatif (Bersih)",
                notes = "Dalam pemantauan penstabilan dos harian di klinik."
            )

            val p3 = PatientEntity(
                id = "p-3",
                regNumber = "MTM/2026/028",
                fullName = "Kugan a/l Subramaniam",
                icNumber = "820309-14-6119",
                age = 44,
                phone = "014-9988776",
                emergencyContact = "011-2233445 (Abang)",
                startDate = "2023-11-01",
                regimen = RegimenType.DOT,
                currentDoseMg = 80.0,
                phase = TreatmentPhase.PENGEKALAN,
                status = PatientStatus.AKTIF,
                dbbDaysAllowed = 0,
                lastUdsDate = "2026-09-01",
                lastUdsResult = "Negatif",
                notes = "AMARAN: Tertinggal 3 hari dos berturut-turut! Hubungi waris & pegawai perubatan."
            )

            val p4 = PatientEntity(
                id = "p-4",
                regNumber = "MTM/2026/033",
                fullName = "Tan Wei Lun",
                icNumber = "861120-10-5143",
                age = 40,
                phone = "016-2233441",
                emergencyContact = "012-7788990 (Rakan kongsi)",
                startDate = "2024-01-20",
                regimen = RegimenType.DOT_DBB,
                currentDoseMg = 90.0,
                phase = TreatmentPhase.PENGEKALAN,
                status = PatientStatus.AKTIF,
                dbbDaysAllowed = 2,
                lastUdsDate = "2026-09-28",
                lastUdsResult = "Negatif (Bersih)",
                notes = "Kepatuhan cemerlang. Dos bawa balik hujung minggu."
            )

            val p5 = PatientEntity(
                id = "p-5",
                regNumber = "MTM/2026/045",
                fullName = "Zulkifli bin Hashim",
                icNumber = "790405-01-5789",
                age = 47,
                phone = "018-3344552",
                emergencyContact = "017-9911223 (Anak)",
                startDate = "2026-09-15",
                regimen = RegimenType.DOT,
                currentDoseMg = 45.0,
                phase = TreatmentPhase.INDUKSI,
                status = PatientStatus.AKTIF,
                dbbDaysAllowed = 0,
                lastUdsDate = "2026-09-15",
                lastUdsResult = "Positif Opiat (Awal kemasukan)",
                notes = "Fasa induksi awal. Pantau ketat tindak balas sampingan dan sedasi."
            )

            val p6 = PatientEntity(
                id = "p-6",
                regNumber = "MTM/2026/052",
                fullName = "Azlan bin Mansor",
                icNumber = "880818-10-5441",
                age = 38,
                phone = "019-5566778",
                emergencyContact = "019-3322110 (Isteri)",
                startDate = "2024-06-12",
                regimen = RegimenType.DOT_DBB,
                currentDoseMg = 70.0,
                phase = TreatmentPhase.PENGEKALAN,
                status = PatientStatus.AKTIF,
                dbbDaysAllowed = 4,
                lastUdsDate = "2026-08-20", // > 30 days ago, triggers UDS alert
                lastUdsResult = "Negatif",
                notes = "Perlu ujian saringan urin (UDS) berkala segera."
            )

            pDao.insertPatients(listOf(p1, p2, p3, p4, p5, p6))

            // Dispensations:
            // p1: today attended DOT + DBB
            val d1 = DispensationEntity(
                id = "disp-1",
                patientId = p1.id,
                patientName = p1.fullName,
                date = todayStr,
                timestamp = System.currentTimeMillis() - 3600000 * 2,
                status = DispensationStatus.HADIR_DOT_DBB,
                dotDoseMg = 75.0,
                dbbDoseMg = 75.0,
                dbbDaysGiven = 3,
                dispenserName = "Farah (Farmasi)",
                staffNotes = "Minum di depan staf 75mg. 3 botol DBB dilabel dan diserahkan."
            )

            // p2: attended today DOT
            val d2 = DispensationEntity(
                id = "disp-2",
                patientId = p2.id,
                patientName = p2.fullName,
                date = todayStr,
                timestamp = System.currentTimeMillis() - 3600000 * 1,
                status = DispensationStatus.HADIR_DOT,
                dotDoseMg = 60.0,
                dbbDoseMg = 0.0,
                dbbDaysGiven = 0,
                dispenserName = "Farah (Farmasi)",
                staffNotes = "Minum habis dengan segelas air. Keadaan sihat."
            )

            // p3: missed today, missed yesterday, missed day3 -> triggers 3 consecutive days!
            val d3Today = DispensationEntity(
                id = "disp-3-today",
                patientId = p3.id,
                patientName = p3.fullName,
                date = todayStr,
                timestamp = System.currentTimeMillis(),
                status = DispensationStatus.TIDAK_HADIR_MISSED,
                dotDoseMg = 0.0,
                dbbDoseMg = 0.0,
                dbbDaysGiven = 0,
                dispenserName = "Sistem Amaran",
                staffNotes = "Hari ke-3 tidak hadir. Toleransi opioid susut. Dilarang dispens!"
            )
            val d3Yest = DispensationEntity(
                id = "disp-3-yest",
                patientId = p3.id,
                patientName = p3.fullName,
                date = yesterdayStr,
                timestamp = System.currentTimeMillis() - 86400000,
                status = DispensationStatus.TIDAK_HADIR_MISSED,
                dotDoseMg = 0.0,
                dbbDoseMg = 0.0,
                dbbDaysGiven = 0,
                dispenserName = "Sistem Amaran",
                staffNotes = "Hari ke-2 missed dose."
            )
            val d3Day2 = DispensationEntity(
                id = "disp-3-day2",
                patientId = p3.id,
                patientName = p3.fullName,
                date = day2Str,
                timestamp = System.currentTimeMillis() - 86400000 * 2,
                status = DispensationStatus.TIDAK_HADIR_MISSED,
                dotDoseMg = 0.0,
                dbbDoseMg = 0.0,
                dbbDaysGiven = 0,
                dispenserName = "Sistem Amaran",
                staffNotes = "Hari ke-1 missed dose."
            )

            // p5: yesterday missed
            val d5Yest = DispensationEntity(
                id = "disp-5-yest",
                patientId = p5.id,
                patientName = p5.fullName,
                date = yesterdayStr,
                timestamp = System.currentTimeMillis() - 86400000,
                status = DispensationStatus.TIDAK_HADIR_MISSED,
                dotDoseMg = 0.0,
                dbbDoseMg = 0.0,
                dbbDaysGiven = 0,
                dispenserName = "Sistem Amaran",
                staffNotes = "Pesakit maklum ada urusan kecemasan keluarga."
            )

            dDao.insertDispensations(listOf(d1, d2, d3Today, d3Yest, d3Day2, d5Yest))

            // Initial Follow Up Appointments
            val a1 = AppointmentEntity(
                id = "apt-1",
                patientId = p3.id,
                patientName = p3.fullName,
                regNumber = p3.regNumber,
                appointmentType = AppointmentType.SEMAKAN_DOKTOR,
                scheduledDate = todayStr,
                scheduledTime = "11:30 AM",
                status = AppointmentStatus.TERTUNGGAK,
                doctorNotes = "Penilaian semula segera toleransi dos kerana tidak hadir 3 hari.",
                isAutoGenerated = true
            )

            val a2 = AppointmentEntity(
                id = "apt-2",
                patientId = p6.id,
                patientName = p6.fullName,
                regNumber = p6.regNumber,
                appointmentType = AppointmentType.UJIAN_UDS,
                scheduledDate = nextDate1,
                scheduledTime = "09:00 AM",
                status = AppointmentStatus.AKAN_DATANG,
                doctorNotes = "Ujian saringan dadah dalam air kencing (UDS) bulanan.",
                isAutoGenerated = true
            )

            val a3 = AppointmentEntity(
                id = "apt-3",
                patientId = p1.id,
                patientName = p1.fullName,
                regNumber = p1.regNumber,
                appointmentType = AppointmentType.PENILAIAN_DBB,
                scheduledDate = nextDate2,
                scheduledTime = "10:00 AM",
                status = AppointmentStatus.AKAN_DATANG,
                doctorNotes = "Penilaian pembaharuan kebenaran Dos Bawa Balik (DBB 3 Hari).",
                isAutoGenerated = true
            )

            aDao.insertAppointments(listOf(a1, a2, a3))
        }
    }
}
