package net.azarquiel.cuidaplusjpc.model

import java.util.Date

data class CitaMedica(
    var citaMedicaId: String = "",             // ID único de la cita
    var grupoFamiliarId: String = "",          // ID del grupo familiar asociado
    var pacienteId: String = "",               // ID del paciente que tiene la cita
    var usuarioAcompananteNombre: String = "", // Nombre del acompañante (familiar)
    var fechaHora: Date = Date(),              // Fecha y hora de la cita
    var especialidad: String = "",             // Especialidad médica
    var medico: String = "",                   // Nombre del médico
    var ubicacion: String = "",                // Lugar donde se realiza la cita
    var motivo: String = "",                   // Motivo de la cita
    var observaciones: String = "",            // Observaciones adicionales
    var realizada: Boolean = false             // True si ya se realizó
)





