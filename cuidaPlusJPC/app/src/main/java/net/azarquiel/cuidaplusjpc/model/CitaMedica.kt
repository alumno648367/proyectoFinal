package net.azarquiel.cuidaplusjpc.model

data class CitaMedica(
    var citaMedicaId: String = "",         // ID único
    var pacienteId: String = "",           // Relación con paciente
    var fecha: String = "",                // Ej: "2025-06-12T10:30", o usar Date/Timestamp
    var especialidad: String = "",         // Ej: "Cardiología"
    var medico: String = "",               // Nombre del médico
    var ubicacion: String = "",            // Hospital o consulta
    var motivo: String = "",               // Motivo de la cita
    var observaciones: String = "",
    var estado: String = ""                // Ej: "Pendiente", "Realizada", "Cancelada"
)
