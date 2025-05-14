package net.azarquiel.cuidaplusjpc.model

data class Tratamiento(
    var tratamientoId: String = "",                  // ID único
    var enfermedadPacienteId: String = "",           // Relación con EnfermedadPaciente
    var nombre: String = "",                         // Nombre del tratamiento
    var tipo: String = "",                           // Ej: "Farmacológico", "Fisioterapia", etc.
    var inicio: String = "",                         // Fecha de inicio (String o Date)
    var fin: String = "",                            // Fecha de fin si aplica
    var descripcion: String = "",
    var medicamentos: List<String> = emptyList()     // Lista de IDs o usar subcolección
)
