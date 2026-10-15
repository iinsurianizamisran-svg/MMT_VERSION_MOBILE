package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    @Query("SELECT * FROM patients ORDER BY fullName ASC")
    fun getAllPatients(): Flow<List<PatientEntity>>

    @Query("SELECT * FROM patients WHERE id = :id")
    suspend fun getPatientById(id: String): PatientEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: PatientEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatients(patients: List<PatientEntity>)

    @Update
    suspend fun updatePatient(patient: PatientEntity)

    @Query("DELETE FROM patients WHERE id = :id")
    suspend fun deletePatient(id: String)
}

@Dao
interface DispensationDao {
    @Query("SELECT * FROM dispensations ORDER BY timestamp DESC")
    fun getAllDispensations(): Flow<List<DispensationEntity>>

    @Query("SELECT * FROM dispensations WHERE patientId = :patientId ORDER BY timestamp DESC")
    fun getDispensationsForPatient(patientId: String): Flow<List<DispensationEntity>>

    @Query("SELECT * FROM dispensations WHERE date = :date")
    suspend fun getDispensationsForDate(date: String): List<DispensationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDispensation(dispensation: DispensationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDispensations(dispensations: List<DispensationEntity>)
}

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments ORDER BY scheduledDate ASC, scheduledTime ASC")
    fun getAllAppointments(): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE patientId = :patientId ORDER BY scheduledDate ASC")
    fun getAppointmentsForPatient(patientId: String): Flow<List<AppointmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: AppointmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointments(appointments: List<AppointmentEntity>)

    @Update
    suspend fun updateAppointment(appointment: AppointmentEntity)

    @Query("DELETE FROM appointments WHERE id = :id")
    suspend fun deleteAppointment(id: String)
}
