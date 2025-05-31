package net.azarquiel.cuidaplusjpc.model

import java.util.Date

data class Tratamiento(
    var tratamientoId: String = "",                 // ID único del tratamiento
    var enfermedadPacienteId: String = "",          // ID de la enfermedad del paciente asociada
    var nombre: String = "",                        // Nombre del tratamiento
    var tipo: String = "",                          // Tipo (ej: Farmacológico, Fisioterapia, etc.)
    var inicio: Date = Date(),                      // Fecha de inicio
    var fin: Date = Date(),                         // Fecha de fin (si aplica)
    var descripcion: String = ""                    // Descripción o notas del tratamiento
)
