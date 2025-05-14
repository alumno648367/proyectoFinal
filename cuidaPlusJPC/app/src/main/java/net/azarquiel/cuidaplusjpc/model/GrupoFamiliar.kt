package net.azarquiel.cuidaplusjpc.model

data class GrupoFamiliar(
    var grupoFamiliarId: String = "",
    var nombre: String = "",
    var miembros: List<String> = emptyList(),     // Lista de usuarioId
    var pacientes: List<String> = emptyList()     // Lista de pacienteId
)
