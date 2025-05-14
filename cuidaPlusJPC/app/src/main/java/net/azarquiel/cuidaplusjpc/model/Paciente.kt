package net.azarquiel.cuidaplusjpc.model

data class Paciente(
    var pacienteId: String = "",               // ID único
    var grupoFamiliarId: String = "",          // A qué grupo pertenece
    var nombre: String = "",
    var fechaNacimiento: String = "",          // Puedes usar Date si configuras el adaptador
    var archivosAdjuntos: List<String> = emptyList()  // Lista de IDs o URLs si los tienes fuera
)
