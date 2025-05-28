package net.azarquiel.cuidaplus.model

import java.util.Date

data class Paciente(
    var pacienteId: String = "",               // ID único
    var grupoFamiliarId: String = "",
    var nombreGrupo: String = "",// A qué grupo pertenece
    var nombreCompleto: String = "",
    var fechaNacimiento: Date = Date(),
    var direccion: String = "",
    var enfermedades: List<String> = emptyList() // IDs de enfermedades
    //var archivosAdjuntos: List<String> = emptyList(), // Lista de IDs o URLs si los tienes fuera

)
