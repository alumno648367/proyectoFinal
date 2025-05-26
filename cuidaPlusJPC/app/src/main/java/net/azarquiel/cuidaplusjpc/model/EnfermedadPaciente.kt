package net.azarquiel.cuidaplusjpc.model

data class EnfermedadPaciente(
    var enfermedadPacienteId: String = "",
    var pacienteId: String = "",
    var enfermedadId: String = "",
    var nombre: String = "",
    var categoria: String = "",
    var fechaDiagnostico: String = "",
    var estado: String = "",
    var observaciones: String = ""
)
