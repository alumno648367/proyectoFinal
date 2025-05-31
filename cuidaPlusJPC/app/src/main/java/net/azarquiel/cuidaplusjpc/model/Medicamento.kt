package net.azarquiel.cuidaplusjpc.model

data class Medicamento(
    var medicamentoId: String = "",                  // ID único del medicamento asignado
    var enfermedadPacienteId: String = "",           // ID de la enfermedad del paciente a la que pertenece
    var nombre: String = "",                         // Nombre del medicamento
    var dosis: String = "",                          // Ej: "500 mg"
    var frecuencia: String = "",                     // Ej: "Cada 8 horas"
    var viaAdministracion: String = "",              // Ej: "Oral", "Intravenosa"
    var horario: List<String> = emptyList(),         // Líneas futuras
    var observaciones: String = ""                   // Notas adicionales
)
