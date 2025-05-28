package net.azarquiel.cuidaplusjpc.model

data class Medicamento(
    var medicamentoId: String = "",
    var enfermedadPacienteId: String = "",
    var nombre: String = "",
    var dosis: String = "",
    var frecuencia: String = "",
    var viaAdministracion: String = "",
    var observaciones: String = ""
)
