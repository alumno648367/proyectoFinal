package net.azarquiel.cuidaplusjpc.model

data class GrupoFamiliar(
    var grupoFamiliarId: String = "",             // ID único del grupo familiar
    var nombre: String = "",                      // Nombre del grupo (ej: Familia Gómez)
    var miembros: List<String> = emptyList(),     // Lista de IDs de usuarios que pertenecen al grupo
    var pacientes: List<String> = emptyList()     // Lista de IDs de pacientes asociados al grupo
)
