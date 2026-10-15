package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.AppointmentStatus
import com.example.data.model.AppointmentType
import com.example.data.model.DispensationStatus
import com.example.data.model.PatientStatus
import com.example.data.model.RegimenType
import com.example.data.model.TreatmentPhase

class Converters {
    @TypeConverter
    fun fromRegimenType(value: RegimenType?): String? = value?.name

    @TypeConverter
    fun toRegimenType(value: String?): RegimenType? =
        value?.let { runCatching { RegimenType.valueOf(it) }.getOrDefault(RegimenType.DOT) }

    @TypeConverter
    fun fromTreatmentPhase(value: TreatmentPhase?): String? = value?.name

    @TypeConverter
    fun toTreatmentPhase(value: String?): TreatmentPhase? =
        value?.let { runCatching { TreatmentPhase.valueOf(it) }.getOrDefault(TreatmentPhase.PENGEKALAN) }

    @TypeConverter
    fun fromPatientStatus(value: PatientStatus?): String? = value?.name

    @TypeConverter
    fun toPatientStatus(value: String?): PatientStatus? =
        value?.let { runCatching { PatientStatus.valueOf(it) }.getOrDefault(PatientStatus.AKTIF) }

    @TypeConverter
    fun fromDispensationStatus(value: DispensationStatus?): String? = value?.name

    @TypeConverter
    fun toDispensationStatus(value: String?): DispensationStatus? =
        value?.let { runCatching { DispensationStatus.valueOf(it) }.getOrDefault(DispensationStatus.HADIR_DOT) }

    @TypeConverter
    fun fromAppointmentType(value: AppointmentType?): String? = value?.name

    @TypeConverter
    fun toAppointmentType(value: String?): AppointmentType? =
        value?.let { runCatching { AppointmentType.valueOf(it) }.getOrDefault(AppointmentType.SEMAKAN_DOKTOR) }

    @TypeConverter
    fun fromAppointmentStatus(value: AppointmentStatus?): String? = value?.name

    @TypeConverter
    fun toAppointmentStatus(value: String?): AppointmentStatus? =
        value?.let { runCatching { AppointmentStatus.valueOf(it) }.getOrDefault(AppointmentStatus.AKAN_DATANG) }
}
