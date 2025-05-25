package net.azarquiel.cuidaplusjpc.model

import java.util.Date

data class Paciente(
    var pacienteId: String = "",               // ID único
    var grupoFamiliarId: String = "",
    var nombreGrupo: String = "",// A qué grupo pertenece
    var nombre: String = "",
    var fechaNacimiento: Date = Date(),
    var archivosAdjuntos: List<String> = emptyList()  // Lista de IDs o URLs si los tienes fuera
)
