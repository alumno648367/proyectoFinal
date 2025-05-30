package net.azarquiel.cuidaplusjpc.model

import java.util.Date

data class CitaMedica(
    var citaMedicaId: String = "",
    var grupoFamiliarId: String = "",
    var pacienteId: String = "",
    var usuarioAcompananteNombre: String = "",
    var fechaHora: Date = Date(),
    var especialidad: String = "",
    var medico: String = "",
    var ubicacion: String = "",
    var motivo: String = "",
    var observaciones: String = "",
    var realizada: Boolean = false             // True si ya se realizó
)
