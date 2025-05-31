package net.azarquiel.cuidaplusjpc.model

import java.util.Date

data class Paciente(
    var pacienteId: String = "",                       // ID único del paciente
    var grupoFamiliarId: String = "",                  // ID del grupo al que pertenece
    var nombreGrupo: String = "",                      // Nombre del grupo (copiado para evitar lecturas extra)
    var nombreCompleto: String = "",                   // Nombre completo del paciente
    var fechaNacimiento: Date = Date(),                // Fecha de nacimiento
    var direccion: String = "",                        // Dirección del paciente
    var enfermedades: List<String> = emptyList(),      // Lista de IDs de enfermedades asociadas
    var archivosAdjuntos: List<String> = emptyList()   // Líneas futuras
)
