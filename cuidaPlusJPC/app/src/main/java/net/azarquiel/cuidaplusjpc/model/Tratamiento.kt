package net.azarquiel.cuidaplusjpc.model

import java.util.Date

data class Tratamiento(
    var tratamientoId: String = "",                  // ID único
    var enfermedadPacienteId: String = "",           // Relación con EnfermedadPaciente
    var nombre: String = "",                         // Nombre del tratamiento
    var tipo: String = "",                           // Ej: "Farmacológico", "Fisioterapia", etc.
    var inicio: Date = Date(),                         // Fecha de inicio (String o Date)
    var fin: Date = Date(),                            // Fecha de fin si aplica
    var descripcion: String = ""
    )
