package net.azarquiel.cuidaplusjpc.model

data class CitaMedica(
    var citaMedicaId: String = "",
    var pacienteId: String = "",
    var usuarioAcompananteId: String = "",
    var fechaHora: Long = 0L,              // Timestamp en milisegundos
    var especialidad: String = "",
    var medico: String = "",
    var ubicacion: String = "",
    var motivo: String = "",
    var observaciones: String = "",
    var realizada: Boolean = false         // True si ya se realizó
)
