package net.azarquiel.cuidaplus.model

data class HistorialMedico(
    var historialId: String = "",                       // ID único (puede coincidir con pacienteId)
    var pacienteId: String = "",                        // Relación con el paciente
    var alergias: List<String> = emptyList(),
    var enfermedadesCronicas: List<String> = emptyList(),
    var operaciones: List<String> = emptyList(),
    var antecedentesFamiliares: List<String> = emptyList(),
    var vacunas: List<String> = emptyList(),
    var grupoSanguineo: String = "",
    var observaciones: String = "",
    var fechaUltimaActualizacion: String = ""           // O usa Timestamp si lo prefieres
)
